package io.silv.pootracker.sync

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


actual object CommonSyncWorker: KoinComponent {

    private const val TAG = "sync_work"

    actual fun start() {
        val request = OneTimeWorkRequestBuilder<AndroidSyncWorker>()
            .addTag(TAG)
            .build()

        WorkManager.getInstance(get())
            .enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
    }

    actual fun stop() {
        WorkManager.getInstance(get())
            .cancelUniqueWork(TAG)
    }

    actual fun isRunning(): Boolean {
        return runBlocking {
            WorkManager.getInstance(get())
                .getWorkInfosByTagFlow(TAG)
                .first()
                .let { list -> list.any { it.state == WorkInfo.State.RUNNING } }
        }
    }

    actual fun isRunningFlow(): Flow<Boolean> {
        return WorkManager.getInstance(get())
            .getWorkInfosByTagFlow(TAG)
            .map { list -> list.any { it.state == WorkInfo.State.RUNNING } }
    }

}