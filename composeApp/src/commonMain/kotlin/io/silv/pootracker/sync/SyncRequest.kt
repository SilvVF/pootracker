package io.silv.pootracker.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SyncRequest(
    val id: Long,
    val logId: String,
    val userId: String,
) {

    private val _statusFlow = MutableStateFlow(State.NOT_SYNCED)

    val statusFlow = _statusFlow.asStateFlow()
    var status: State
        get() = _statusFlow.value
        set(status) {
            _statusFlow.value = status
        }


    enum class State(val value: Int) {
        NOT_SYNCED(0),
        QUEUE(1),
        SYNCING(2),
        SYNCED(3),
        ERROR(4),
    }
}