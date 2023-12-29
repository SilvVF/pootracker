package io.silv.pootracker.sync

import io.silv.pootracker.util.ApplicationScope
import kotlinx.coroutines.launch


class SyncManager(
    private val applicationScope: ApplicationScope,
    private val store: SyncStore
) {

    private val syncer = Syncer(applicationScope, store)

    val isRunning: Boolean
        get() = syncer.isRunning

    val queueState
        get() = syncer.queueState

    fun stopSync(reason: String) = applicationScope.launch {

    }

    fun startSync() = applicationScope.launch {

    }
}