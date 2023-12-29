package io.silv.pootracker.ui.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.silv.pootracker.data.GetPoopLogs
import io.silv.pootracker.data.PoopLogHandler
import io.silv.pootracker.models.DomainPoopLog
import io.silv.pootracker.network.SupabaseApi
import io.silv.pootracker.util.MoleculeEffectScreenModel
import io.silv.pootracker.util.mapEach
import io.silv.pootracker.util.uuid
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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
        val logs: List<DomainPoopLog>,
        val actions: LogsAction,
        val status: SessionStatus
    ): LogsModel(status)
}

class LogsScreenModel(
    private val getPoopLogs: GetPoopLogs,
    private val poopLogHandler: PoopLogHandler,
    private val api: SupabaseApi,
    private val auth: Auth
): MoleculeEffectScreenModel<LogsEvent, LogsModel>() {

    @Composable
    override fun models(events: SendChannel<LogsEvent>): LogsModel {
        return logsPresenter(
            getPoopLogs,
            poopLogHandler,
            api,
            auth,
            events
        )
    }
}

@Composable
private fun logsPresenter(
    getPoopLogs: GetPoopLogs,
    poopLogHandler: PoopLogHandler,
    api: SupabaseApi,
    auth: Auth,
    events: SendChannel<LogsEvent>,
): LogsModel {

    val scope = rememberCoroutineScope()

    val status by auth.sessionStatus.collectAsState()


    val logs by produceState<List<DomainPoopLog>?>(null){
        getPoopLogs.subscribe()
            .mapEach(::DomainPoopLog)
            .catch {
                events.send(LogsEvent.ErrorReceivingLogs)

            }.onEach { value = it }
            .launchIn(this)
    }

    fun addLog() {
        scope.launch {
            poopLogHandler.insert(
                uuid(),
                Clock.System.now(),
                createdBy = (status as? SessionStatus.Authenticated)
                    ?.session
                    ?.user
                    ?.id
                    ?: return@launch
            )
        }
    }

    return logs?.let {
        LogsModel.Success(
            logs = it,
            status = status,
            actions = LogsAction(
                add = { addLog() }
            )
        )
    }
        ?: LogsModel.Loading(status)
}


