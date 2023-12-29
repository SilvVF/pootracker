package io.silv.pootracker.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.silv.pootracker.util.DefaultDispatcher
import io.silv.pootracker.util.NetworkConnectivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidSyncWorker(
    context: Context,
    workerParameters: WorkerParameters,
): CoroutineWorker(context, workerParameters), KoinComponent {

    private val networkConnectivity: NetworkConnectivity by inject()
    private val defaultDispatcher: DefaultDispatcher by inject()
    private val syncManager : SyncManager by inject()

    private val syncJob = SyncJob(syncManager, networkConnectivity, defaultDispatcher)

    override suspend fun doWork(): Result {

        return if (syncJob.doWork().isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}