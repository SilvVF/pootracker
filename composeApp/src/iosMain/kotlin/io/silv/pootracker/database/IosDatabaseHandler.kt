package io.silv.pootracker.database

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import io.silv.Database
import io.silv.pootracker.data.DatabaseHandler
import io.silv.pootracker.util.IODispatcher
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume


actual class RealDatabaseHandler: DatabaseHandler, KoinComponent {

    actual val db: Database = get()
    actual val driver: SqlDriver = get()
    actual val queryDispatcher: IODispatcher = get()
    actual val transactionDispatcher: IODispatcher = queryDispatcher

    val suspendingTransactionId  = object : CoroutineContext {
        override fun <R> fold(
            initial: R, operation: (R, CoroutineContext.Element) -> R): R {
            return initial
        }

        override fun <E : CoroutineContext.Element> get(
            key: CoroutineContext.Key<E>): E? {
         return null
        }

        override fun minusKey(
            key: CoroutineContext.Key<*>): CoroutineContext {
          return this
        }

    }

    override suspend fun <T> await(inTransaction: Boolean, block: suspend Database.() -> T): T {
        return dispatch(inTransaction, block)
    }

    override suspend fun <T : Any> awaitList(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): List<T> {
        return dispatch(inTransaction) { block(db).executeAsList() }
    }

    override suspend fun <T : Any> awaitOne(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): T {
        return dispatch(inTransaction) { block(db).executeAsOne() }
    }

    override suspend fun <T : Any> awaitOneExecutable(
        inTransaction: Boolean,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T {
        return dispatch(inTransaction) { block(db).executeAsOne() }
    }

    override suspend fun <T : Any> awaitOneOrNull(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): T? {
        return dispatch(inTransaction) { block(db).executeAsOneOrNull() }
    }

    override suspend fun <T : Any> awaitOneOrNullExecutable(
        inTransaction: Boolean,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T? {
        return dispatch(inTransaction) { block(db).executeAsOneOrNull() }
    }

    override fun <T : Any> subscribeToList(block: Database.() -> Query<T>): Flow<List<T>> {
        return block(db).asFlow().mapToList(queryDispatcher)
    }

    override fun <T : Any> subscribeToOne(block: Database.() -> Query<T>): Flow<T> {
        return block(db).asFlow().mapToOne(queryDispatcher)
    }

    override fun <T : Any> subscribeToOneOrNull(block: Database.() -> Query<T>): Flow<T?> {
        return block(db).asFlow().mapToOneOrNull(queryDispatcher)
    }

    private suspend fun <T> dispatch(inTransaction: Boolean, block: suspend Database.() -> T): T {
        // Create a transaction if needed and run the calling block inside it.
        if (inTransaction) {
            return withTransaction { block(db) }
        }

        // If we're currently in the transaction thread, there's no need to dispatch our query.
        if (driver.currentTransaction() != null) {
            return block(db)
        }

        // Get the current database context and run the calling block.
        val context = getCurrentDatabaseContext()
        return withContext(context) { block(db) }
    }
}

/**
 * Returns the transaction dispatcher if we are on a transaction, or the database dispatchers.
 */
internal suspend fun RealDatabaseHandler.getCurrentDatabaseContext(): CoroutineContext {
    return coroutineContext[TransactionElement]?.transactionDispatcher ?: queryDispatcher
}

/**
 * Calls the specified suspending [block] in a database transaction. The transaction will be
 * marked as successful unless an exception is thrown in the suspending [block] or the coroutine
 * is cancelled.
 *
 * SQLDelight will only perform at most one transaction at a time, additional transactions are queued
 * and executed on a first come, first serve order.
 *
 * Performing blocking database operations is not permitted in a coroutine scope other than the
 * one received by the suspending block. It is recommended that all [Dao] function invoked within
 * the [block] be suspending functions.
 *
 * The dispatcher used to execute the given [block] will utilize threads from SQLDelight's query executor.
 */
internal suspend fun <T> RealDatabaseHandler.withTransaction(block: suspend () -> T): T {
    // Use inherited transaction context if available, this allows nested suspending transactions.
    val transactionContext =
        coroutineContext[TransactionElement]?.transactionDispatcher ?: createTransactionContext()
    return withContext(transactionContext) {
        val transactionElement = coroutineContext[TransactionElement]!!
        transactionElement.acquire()
        try {
            db.transactionWithResult {
                runBlocking(transactionContext) {
                    block()
                }
            }
        } finally {
            transactionElement.release()
        }
    }
}

/**
 * Creates a [CoroutineContext] for performing database operations within a coroutine transaction.
 *
 * The context is a combination of a dispatcher, a [TransactionElement] and a thread local element.
 *
 * * The dispatcher will dispatch coroutines to a single thread that is taken over from the SQLDelight
 * query executor. If the coroutine context is switched, suspending DAO functions will be able to
 * dispatch to the transaction thread.
 *
 * * The [TransactionElement] serves as an indicator for inherited context, meaning, if there is a
 * switch of context, suspending DAO methods will be able to use the indicator to dispatch the
 * database operation to the transaction thread.
 *
 * * The thread local element serves as a second indicator and marks threads that are used to
 * execute coroutines within the coroutine transaction, more specifically it allows us to identify
 * if a blocking DAO method is invoked within the transaction coroutine. Never assign meaning to
 * this value, for now all we care is if its present or not.
 */
private suspend fun RealDatabaseHandler.createTransactionContext(): CoroutineContext {
    val controlJob = Job()
    // make sure to tie the control job to this context to avoid blocking the transaction if
    // context get cancelled before we can even start using this job. Otherwise, the acquired
    // transaction thread will forever wait for the controlJob to be cancelled.
    // see b/148181325
    coroutineContext[Job]?.invokeOnCompletion {
        controlJob.cancel()
    }

    val dispatcher = transactionDispatcher.acquireTransactionThread(controlJob)
    val transactionElement = TransactionElement(controlJob, dispatcher)
    val threadLocalElement = suspendingTransactionId

    return dispatcher + transactionElement + threadLocalElement
}

/**
 * Acquires a thread from the executor and returns a [ContinuationInterceptor] to dispatch
 * coroutines to the acquired thread. The [controlJob] is used to control the release of the
 * thread by cancelling the job.
 */
private suspend fun CoroutineDispatcher.acquireTransactionThread(
    controlJob: Job,
): ContinuationInterceptor {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            // We got cancelled while waiting to acquire a thread, we can't stop our attempt to
            // acquire a thread, but we can cancel the controlling job so once it gets acquired it
            // is quickly released.
            controlJob.cancel()
        }
        try {
            dispatch(
                EmptyCoroutineContext,
                block = Runnable {
                    runBlocking {
                        // Thread acquired, resume coroutine
                        continuation.resume(kotlin.coroutines.coroutineContext[ContinuationInterceptor]!!)
                        controlJob.join()
                    }
                }
            )
        } catch (ex: Exception) {
            // Couldn't acquire a thread, cancel coroutine
            continuation.cancel(
                IllegalStateException(
                    "Unable to acquire a thread to perform the database transaction",
                    ex,
                ),
            )
        }
    }
}

/**
 * A [CoroutineContext.Element] that indicates there is an on-going database transaction.
 */
private class TransactionElement(
    private val transactionThreadControlJob: Job,
    val transactionDispatcher: ContinuationInterceptor,
) : CoroutineContext.Element {

    companion object Key : CoroutineContext.Key<TransactionElement>

    override val key: CoroutineContext.Key<TransactionElement>
        get() = TransactionElement

    /**
     * Number of transactions (including nested ones) started with this element.
     * Call [acquire] to increase the count and [release] to decrease it. If the count reaches zero
     * when [release] is invoked then the transaction job is cancelled and the transaction thread
     * is released.
     */
    private val referenceCount = atomic(0)

    fun acquire() {
        referenceCount.incrementAndGet()
    }

    fun release() {
        val count = referenceCount.decrementAndGet()
        if (count < 0) {
            throw IllegalStateException("Transaction was never started or was already released")
        } else if (count == 0) {
            // Cancel the job that controls the transaction thread, causing it to be released.
            transactionThreadControlJob.cancel()
        }
    }
}
