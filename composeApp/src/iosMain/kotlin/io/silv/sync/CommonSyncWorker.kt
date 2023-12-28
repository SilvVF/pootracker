package io.silv.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinComponent


actual object CommonSyncWorker: KoinComponent {
    actual fun start() = Unit
    actual fun stop() = Unit
    actual fun isRunning(): Boolean = false
    actual fun isRunningFlow(): Flow<Boolean> = flowOf(false)
}
