package io.silv.pootracker.data.logs

import io.silv.pootracker.data.DatabaseHandler
import io.silv.pootracker.domain.logs.model.Log
import io.silv.pootracker.domain.logs.repository.LogsRepository
import kotlinx.coroutines.flow.Flow

class LogsRepositoryImpl(
    private val handler: DatabaseHandler
): LogsRepository {

    suspend fun getLogById(id: Long): Log {
        return handler.awaitOne { logsQueries.selectById(id, LogsMapper::mapLog) }
    }

    fun getLogByIdAsFlow(id: Long): Flow<Log> {
        return handler.subscribeToOne { logsQueries.selectById(id, LogsMapper::mapLog) }
    }

    suspend fun getLogs(): List<Log> {
        return handler.awaitList { logsQueries.selectAll(LogsMapper::mapLog) }
    }

    fun getLogsAsFlow(): Flow<List<Log>> {
        return handler.subscribeToList { logsQueries.selectAll(LogsMapper::mapLog) }
    }
}