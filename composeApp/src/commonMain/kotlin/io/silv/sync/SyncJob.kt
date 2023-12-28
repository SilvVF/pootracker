package io.silv.sync

import io.silv.util.DefaultDispatcher
import io.silv.util.NetworkConnectivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SyncJob(
    private val syncManager: SyncManager,
    networkConnectivity: NetworkConnectivity,
    defaultDispatcher: DefaultDispatcher,
) {

    private val isOnline = networkConnectivity.online
        .stateIn(
            CoroutineScope(defaultDispatcher),
            SharingStarted.Eagerly,
            true
        )

    suspend fun doWork(): Result<Unit> = runCatching {
        var active = checkConnectivity() && syncManager.syncStart()

        if (!active) {
            return Result.failure()
        }

        // Keep the worker running when needed
        while (active) {
            delay(100)
            active = !isStopped && downloadManager.isRunning && checkConnectivity()
        }

        return Result.success(Unit)
    }

    private fun checkConnectivity(): Boolean {
        return (isOnline.value).also { online ->
            if (!online) {
                syncManager.syncStop("no network connection")
            }
        }
    }
}