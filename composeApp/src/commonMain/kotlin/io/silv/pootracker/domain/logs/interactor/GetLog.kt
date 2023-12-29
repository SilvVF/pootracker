package io.silv.pootracker.domain.logs.interactor

import io.silv.pootracker.data.logs.LogsRepositoryImpl
import io.silv.pootracker.domain.logs.model.Log
import kotlinx.coroutines.flow.Flow

class GetLog (
    private val logsRepository: LogsRepositoryImpl
) {
    suspend fun await(id: Long): Log? {
        return runCatching { logsRepository.getLogById(id) }.getOrNull()
    }

    fun subscribe(id: Long): Flow<Log> {
        return logsRepository.getLogByIdAsFlow(id)
    }
}