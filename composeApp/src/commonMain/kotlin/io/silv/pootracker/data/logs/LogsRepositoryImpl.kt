package io.silv.pootracker.data.logs

import io.silv.pootracker.data.DatabaseHandler
import io.silv.pootracker.domain.logs.model.Log
import io.silv.pootracker.domain.logs.repository.LogsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock

class LogsRepositoryImpl(
    private val handler: DatabaseHandler
): LogsRepository {

    init {
        flow {
            while (true) {
                delay(10000)
                emit(Unit)
            }
        }
            .onEach {
                handler.await(true) {
                    repeat(100) {
                        logsQueries.insert("", "", Clock.System.now(), null, false)
                    }
                }
            }
    }

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