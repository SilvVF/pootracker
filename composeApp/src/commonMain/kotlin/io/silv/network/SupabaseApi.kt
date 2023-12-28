package io.silv.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.silv.util.IODispatcher
import kotlinx.coroutines.withContext


class SupabaseApi(
    private val client: SupabaseClient,
    private val ioDispatcher: IODispatcher
) {

    suspend fun getPoopLogs() = withContext(ioDispatcher) {
        kotlin.runCatching {
            client.from(POOP_LOG_TABLE).select().decodeList<PoopLogDto>()
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