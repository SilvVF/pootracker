package io.silv.pootracker.sync

import io.silv.pootracker.util.DefaultDispatcher
import io.silv.pootracker.util.NetworkConnectivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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

        var active = checkConnectivity()

        if (!active) {
            return Result.success(Unit)
        }

        syncManager.startSync()

        // Keep the worker running when needed
        while (active) {
            delay(100)
            active = syncManager.isRunning && checkConnectivity()
        }

        return Result.success(Unit)
    }

    private fun checkConnectivity(): Boolean {
        return (isOnline.value).also { online ->
            if (!online) {
                syncManager.stopSync("no network connection")
            }
        }
    }
}