package io.silv.sync

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.silv.data.GetPoopLog
import io.silv.data.GetPoopLogs
import io.silv.util.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.concurrent.Volatile

data class SyncRequest(
    val logId: String,
    val userId: String,
) {

    private val _statusFlow = MutableStateFlow(State.NOT_SYNCED)

    val statusFlow = _statusFlow.asStateFlow()
    var status: State
        get() = _statusFlow.value
        set(status) {
            _statusFlow.value = status
        }


    enum class State(val value: Int) {
        NOT_SYNCED(0),
        QUEUE(1),
        SYNCING(2),
        SYNCED(3),
        ERROR(4),
    }
}

/**
 * Class used for sync serialization
 *
 * @param logId the id of the poop log.
 * @param userId the id of the user.
 * @param order the order of the download in the queue.
 */
@Serializable
data class SyncObject(val logId: String, val userId: String, val order: Int)


class SyncStore(
    private val settings: Settings,
    private val getPoopLog: GetPoopLog
) {
    /**
     * Counter used to keep the queue order.
     */
    private var counter = 0

    /**
     * Adds a list of downloads to the store.
     *
     * @param requests the list of downloads to add.
     */
    fun addAll(requests: List<SyncRequest>) {
        requests.forEach { request ->
            settings[getKey(request)] = serialize(request)
        }
    }
    /**
     * Removes a download from the store.
     *
     * @param request the download to remove.
     */
    fun remove(request: SyncRequest) {
        settings.remove(getKey(request))
    }

    /**
     * Removes a list of requests from the store.
     *
     * @param requests the download to remove.
     */
    fun removeAll(requests: List<SyncRequest>) {
        requests.forEach { request ->
            settings.remove(getKey(request))
        }
    }

    /**
     * Returns the preference's key for the given download.
     *
     * @param request the sync request.
     */
    private fun getKey(request: SyncRequest): String {
        return request.logId
    }

    /**
     * Removes all the requests from the store.
     */
    fun clear() {
        settings.clear()
    }

    /**
     * Returns the list of downloads to restore. It should be called in a background thread.
     */
    suspend fun restore(): List<SyncRequest> {
        val objs = settings.keys
                .mapNotNull {
                    deserialize(
                        settings.getString(it, "")
                    )
                }
                .sortedBy { it.order }

        val requests = mutableListOf<SyncRequest>()
        if (objs.isNotEmpty()) {
            for ((logId, userId) in objs) {

                val log = getPoopLog.await(logId)

                requests.add(SyncRequest(logId, userId))
            }
        }

        // Clear the store, downloads will be added again immediately.
        clear()
        return requests
    }

    /**
     * Converts a syncRequest to a string.
     *
     * @param request the SyncRequest to serialize.
     */
    private fun serialize(request: SyncRequest): String {
        val obj = SyncObject(request.logId, request.userId, counter++)
        return try {
            Json.encodeToString(obj)
        } catch (e: SerializationException) {
            Logger.d(e.stackTraceToString())
            ""
        }
    }


    /**
     * Restore a syncRequest from a string.
     *
     * @param string the syncRequest as string.
     */
    private fun deserialize(string: String): SyncObject? {
        return try {
            Json.decodeFromString<SyncObject>(string)
        } catch (e: Exception) {
            Logger.d(e.stackTraceToString())
            null
        }
    }
}

class Syncer(
    private val applicationScope: ApplicationScope,
    private val store: SyncStore
){
    /**
     * Queue where active downloads are kept.
     */
    private val _queueState = MutableStateFlow<List<SyncRequest>>(emptyList())
    val queueState = _queueState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncJob: Job? = null

    /**
     * Whether the downloader is running.
     */
    val isRunning: Boolean
        get() = syncJob?.isActive ?: false
    /**
     * Whether the downloader is paused
     */
    @Volatile
    var isPaused: Boolean = false

    init {
        applicationScope.launch {
            val chapters = async {
                store.restore()
            }
            addAllToQueue(chapters.await())
        }
    }


    private suspend fun addAllToQueue(requests: List<SyncRequest>) {
        _queueState.update {
            requests.forEach { req ->
                req.status = SyncRequest.State.QUEUE
            }
            store.addAll(requests)
            it + requests
        }
    }
}

class SyncManager(
    private val applicationScope: ApplicationScope,
    private val store: SyncStore
) {

    private val syncer = Syncer(applicationScope, store)

    val isRunning: Boolean
        get() = syncer.isRunning

    val queueState
        get() = syncer.queueState

    fun startSync() = applicationScope.launch {
        if ()
    }
}