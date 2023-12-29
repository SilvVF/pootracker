package io.silv.pootracker.network

import io.github.jan.supabase.postgrest.Postgrest
import io.silv.pootracker.util.IODispatcher
import kotlinx.coroutines.withContext


class SupabaseApi(
    private val postgrest: Postgrest,
    private val ioDispatcher: IODispatcher
) {

    suspend fun getPoopLogs() = withContext(ioDispatcher) {
        kotlin.runCatching {
            postgrest.from(POOP_LOG_TABLE).select().decodeList<PoopLogDto>()
        }
            .onFailure {
                it.printStackTrace()
            }
    }

    suspend fun insertPoopLog() = withContext(ioDispatcher) {

    }

    companion object {
        const val POOP_LOG_TABLE = "logs"
    }
}