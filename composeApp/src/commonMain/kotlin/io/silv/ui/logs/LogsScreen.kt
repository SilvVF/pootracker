package io.silv.ui.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import io.silv.util.getPresenter
import io.silv.util.sideEffects

class LogsScreen: Screen {

    @Composable
    override fun Content() {

        val screenModel = getPresenter<LogsScreenModel>()

        val model = screenModel.models.collectAsState().value

        screenModel.sideEffects { effect ->
            when (effect) {
                LogsEvent.ErrorReceivingLogs -> Unit
            }
        }

        Scaffold {
            when(model) {
                is LogsModel.Error -> {}
                LogsModel.Loading -> {}
                is LogsModel.Success -> {
                    LogsSuccessScreen(model, it)
                }
            }
        }
    }
}

@Composable
fun LogsSuccessScreen(
    model: LogsModel.Success,
    paddingValues: PaddingValues,
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            contentPadding = paddingValues
        ) {
            items(model.logs, { it.id }) { log ->
                Text(log.toString(), modifier = Modifier.fillMaxWidth())
            }
        }
        Button(
            onClick = {
                model.actions.add()
            }
        ) {
            Text("add")
        }
    }
}