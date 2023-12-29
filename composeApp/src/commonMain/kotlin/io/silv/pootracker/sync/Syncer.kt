package io.silv.pootracker.sync

import io.silv.pootracker.util.ApplicationScope
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
import kotlin.concurrent.Volatile

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