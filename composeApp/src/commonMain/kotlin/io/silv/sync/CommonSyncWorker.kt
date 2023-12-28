package io.silv.sync

import kotlinx.coroutines.flow.Flow

expect object CommonSyncWorker {

    fun start()
    fun stop()
    fun isRunning(): Boolean
    fun isRunningFlow(): Flow<Boolean>
}
