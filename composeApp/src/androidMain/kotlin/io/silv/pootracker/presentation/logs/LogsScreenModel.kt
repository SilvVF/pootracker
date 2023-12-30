package io.silv.pootracker.presentation.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.silv.pootracker.data.logs.LogsRepositoryImpl
import io.silv.pootracker.domain.logs.interactor.GetLogs
import io.silv.pootracker.domain.logs.model.Log
import io.silv.pootracker.network.SupabaseApi
import io.silv.pootracker.util.MoleculeEffectScreenModel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
data class LogsAction(
    val add: () -> Unit
)

sealed interface LogsEvent {
    data object ErrorReceivingLogs: LogsEvent
}

sealed class LogsModel (
    val sessionStatus: SessionStatus
) {
    data class Loading(val status: SessionStatus): LogsModel(status)

    data class Success(
        val logs: List<Log>,
        val actions: LogsAction,
        val status: SessionStatus
    ): LogsModel(status)
}

class LogsScreenModel(
    private val getPoopLogs: GetLogs,
    private val logsRepository: LogsRepositoryImpl,
    private val api: SupabaseApi,
    private val auth: Auth
): MoleculeEffectScreenModel<LogsEvent, LogsModel>() {

    @Composable
    override fun models(events: SendChannel<LogsEvent>): LogsModel {
        return logsPresenter(
            getPoopLogs,
            api,
            logsRepository,
            auth,
            events
        )
    }
}

@Composable
private fun logsPresenter(
    getPoopLogs: GetLogs,
    api: SupabaseApi,
    logsRepository: LogsRepositoryImpl,
    auth: Auth,
    events: SendChannel<LogsEvent>,
): LogsModel {

    val scope = rememberCoroutineScope()

    val status by auth.sessionStatus.collectAsState()


    val logs by produceState<List<Log>?>(null) {
        getPoopLogs.subscribe().catch {
                events.send(LogsEvent.ErrorReceivingLogs)
            }.onEach { value = it }
            .launchIn(this)
    }

    return logs?.let {
        LogsModel.Success(
            logs = it,
            status = status,
            actions = LogsAction(
                add = {
                    scope.launch {

                    }
                }
            )
        )
    }
        ?: LogsModel.Loading(status)
}


