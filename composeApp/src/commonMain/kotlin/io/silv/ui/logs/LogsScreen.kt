package io.silv.ui.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class LogsScreen: Screen {

    @Composable
    override fun Content() {

        val screenModel = getScreenModel<LogsScreenModel>()

        val model by screenModel.models.collectAsState()

        Column {
            LazyColumn(Modifier.fillMaxWidth().fillMaxHeight(0.8f)) {
                items(model.logs, { it.id }) {
                    Text(it.toString(), modifier = Modifier.fillMaxWidth())
                }
            }
            Button(
                onClick = {
                    screenModel.take(Add)
                }
            ) {
                Text("add")
            }
        }
    }
}