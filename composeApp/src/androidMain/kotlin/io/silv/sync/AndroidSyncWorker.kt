package io.silv.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidSyncWorker(
    context: Context,
    workerParameters: WorkerParameters,
): CoroutineWorker(context, workerParameters), KoinComponent {

    private val syncJob = SyncJob()

    override suspend fun doWork(): Result {

        return if (syncJob.doWork().isSuccess) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}