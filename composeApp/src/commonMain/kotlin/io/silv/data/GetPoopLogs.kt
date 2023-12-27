package io.silv.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.silv.Database
import io.silv.models.DomainPoopLog
import io.silv.network.PoopLogDto
import io.silv.util.IODispatcher
import iosilvsqldelight.PoopLog
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetPoopLogs (
    private val database: Database,
    private val ioDispatcher: IODispatcher
) {
    suspend fun await(): List<PoopLog> =
        withContext(ioDispatcher) {
            database.poopLogQueries
                .selectAll()
                .executeAsList()
        }

    fun subscribe() =
        database.poopLogQueries
            .selectAll()
            .asFlow()
            .mapToList(ioDispatcher)
}