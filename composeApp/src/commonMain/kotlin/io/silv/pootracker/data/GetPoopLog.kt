package io.silv.pootracker.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import io.silv.Database
import io.silv.pootracker.util.IODispatcher
import iosilvsqldelight.PoopLog
import kotlinx.coroutines.withContext

class GetPoopLog (
    private val database: Database,
    private val ioDispatcher: IODispatcher
) {
    suspend fun await(id: String): PoopLog? =
        withContext(ioDispatcher) {
            database.poopLogQueries
                .selectById(id)
                .executeAsOneOrNull()
        }

    fun subscribe(id: String) =
        database.poopLogQueries
            .selectById(id)
            .asFlow()
            .mapToOne(ioDispatcher)
}