package io.silv.ui.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.silv.data.GetPoopLogs
import io.silv.data.PoopLogHandler
import io.silv.models.DomainPoopLog
import io.silv.models.GeoPoint
import io.silv.network.SupabaseApi
import io.silv.util.MoleculeScreenModel
import io.silv.util.uuid
import iosilvsqldelight.PoopLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

sealed interface LogsEvent
data object Add: LogsEvent
data class Delete(val id: Long): LogsEvent

data class LogsModel(
    val logs: List<DomainPoopLog>,
    val loading: Boolean
)

class LogsScreenModel(
    private val getPoopLogs: GetPoopLogs,
    private val poopLogHandler: PoopLogHandler,
    private val api: SupabaseApi
): MoleculeScreenModel<LogsEvent, LogsModel>() {

    @Composable
    override fun models(events: Flow<LogsEvent>): LogsModel {
        return LogsPresenter(getPoopLogs, poopLogHandler, api, events)
    }
}

@Composable
fun LogsPresenter(
    getPoopLogs: GetPoopLogs,
    poopLogHandler: PoopLogHandler,
    api: SupabaseApi,
    events: Flow<LogsEvent>,
): LogsModel {

    var logs by remember { mutableStateOf<List<DomainPoopLog>?>(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                Add -> {
                    poopLogHandler.insert(
                        uuid(), Clock.System.now(), GeoPoint(0.0, 0.0), "dd"
                    )
                }
                is Delete -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        getPoopLogs.subscribe().collect { list ->
            Logger.d { list.toString() }
            logs = list.map { DomainPoopLog(it) }
        }
    }

    return LogsModel(
        logs = logs ?: emptyList(),
        loading = logs == null
    )
}

