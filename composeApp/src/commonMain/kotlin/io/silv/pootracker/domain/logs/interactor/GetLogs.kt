package io.silv.pootracker.domain.logs.interactor

import io.silv.pootracker.data.logs.LogsRepositoryImpl
import io.silv.pootracker.domain.logs.model.Log
import kotlinx.coroutines.flow.Flow

class GetLogs(
    private val logsRepository: LogsRepositoryImpl
) {
    suspend fun await(): List<Log> {
        return logsRepository.getLogs()
    }

    fun subscribe(): Flow<List<Log>> {
        return logsRepository.getLogsAsFlow()
    }
}
