package io.silv.ui.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import io.silv.data.GetPoopLogs
import io.silv.data.PoopLogHandler
import io.silv.models.DomainPoopLog
import io.silv.network.SupabaseApi
import io.silv.util.MoleculeEffectScreenModel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Stable
data class LogsAction(
    val add: () -> Unit
)

sealed interface LogsEvent {
    data object ErrorReceivingLogs: LogsEvent
}

sealed class LogsModel {

    data object Loading: LogsModel()

    data class Success(
        val logs: List<DomainPoopLog>,
        val actions: LogsAction
    ): LogsModel()

    data class Error(
        val reason: String,
        val actions: LogsAction
    ): LogsModel()
}

class LogsScreenModel(
    private val getPoopLogs: GetPoopLogs,
    private val poopLogHandler: PoopLogHandler,
    private val api: SupabaseApi
): MoleculeEffectScreenModel<LogsEvent, LogsModel>() {

    @Composable
    override fun models(events: SendChannel<LogsEvent>): LogsModel {
        return LogsPresenter(getPoopLogs, poopLogHandler, api, events)
    }
}

@Composable
fun LogsPresenter(
    getPoopLogs: GetPoopLogs,
    poopLogHandler: PoopLogHandler,
    api: SupabaseApi,
    events: SendChannel<LogsEvent>,
): LogsModel {

    val logs = getPoopLogs.subscribe()
        .mapEach(::DomainPoopLog)
        .catch {
            events.send(LogsEvent.ErrorReceivingLogs)
        }
        .collectAsState(null).value

    return if (logs == null) {
        LogsModel.Loading
    } else {
        LogsModel.Success(
            logs = logs,
            actions = LogsAction(
                add = {}
            )
        )
    }
}

inline fun <T, V> Flow<Iterable<T>>.mapEach(
    crossinline block: (item: T) -> V
) = map { iterable ->
    iterable.map { item ->
        block(item)
    }
}

