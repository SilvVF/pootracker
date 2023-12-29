package io.silv.pootracker.presentation.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.supabase.gotrue.SessionStatus
import io.silv.pootracker.presentation.auth.AuthScreen
import io.silv.pootracker.presentation.util.collectSideEffectsWithLifecycle

class LogsScreen: Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val screenModel = getScreenModel<LogsScreenModel>()

        val model = screenModel.models.collectAsState().value

        screenModel.collectSideEffectsWithLifecycle { effect ->
            when (effect) {
                LogsEvent.ErrorReceivingLogs -> Unit
            }
        }

        val navigator = LocalNavigator.currentOrThrow

        var popupExpanded by rememberSaveable {
            mutableStateOf(false)
        }

        Scaffold(
            modifier = Modifier.then(
                if (popupExpanded)
                    Modifier.blur(radius = 6.dp)
                else Modifier
            ),
            topBar = {
                LogsTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    sessionStatus = { model.sessionStatus },
                    popupExpanded = { popupExpanded },
                    expandPopup = {
                        popupExpanded = !popupExpanded
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(AuthScreen()) }
                ) {
                    Text("Auth")
                }
            }
        ) {
            when(model) {
                is LogsModel.Loading -> {}
                is LogsModel.Success -> {
                    LogsSuccessScreen(
                        model = { model },
                        paddingValues = it
                    )
                }
            }
        }
    }
}

@Composable
fun AuthToolTipItem(
    modifier: Modifier = Modifier,
    sessionStatus: () -> SessionStatus,
    onLogInClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    val (text, action) = if (sessionStatus() is SessionStatus.Authenticated) {
        "log out" to onLogOutClick
    } else {
        "log in" to onLogInClick
    }

    TopBarDropDownItem(
        text = text,
        icon = Icons.Filled.Person,
        modifier = modifier,
        onClick = { action.invoke() }
    )
}

@Composable
fun TopBarDropDownItem(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(text, style = MaterialTheme.typography.labelLarge)
        },
        onClick = onClick,
        leadingIcon = {
            Icon(icon, text)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsTopBar(
    modifier: Modifier = Modifier,
    popupExpanded: () -> Boolean,
    expandPopup: () -> Unit,
    sessionStatus: () -> SessionStatus
) {
    TopAppBar(
        modifier = modifier,
        title = {
           Text("Home")
        },
        actions = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { expandPopup() }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }

                DropdownMenu(
                    expanded = popupExpanded(),
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .padding(8.dp),
                    offset = DpOffset(x = (12).dp, 0.dp),
                    onDismissRequest = { expandPopup() }
                ) {
                    TopBarDropDownItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Offline mode",
                        icon = Icons.Outlined.Info,
                        onClick = {}
                    )
                    Divider()
                    TopBarDropDownItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Settings",
                        icon = Icons.Outlined.Settings,
                        onClick = {}
                    )
                    AuthToolTipItem(
                        modifier = Modifier.fillMaxWidth(),
                        sessionStatus = sessionStatus,
                        onLogInClick = {},
                        onLogOutClick = {}
                    )
                }
            }
        }
    )
}

@Composable
fun LogsSuccessScreen(
    model: () -> LogsModel.Success,
    paddingValues: PaddingValues,
) {
    Column {
        PoopLogsList(
            modifier = Modifier.fillMaxWidth(),
            model = model,
            paddingValues = paddingValues
        )
        Button(
            onClick = {
                model().actions.add()
            }
        ) {
            Text("add")
        }
    }
}

@Composable
fun PoopLogsList(
    modifier: Modifier = Modifier,
    model: () -> LogsModel.Success,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues
    ) {

        items(model().logs, { it.id }) { log ->
            Text(
                log.toString(), modifier = Modifier.fillMaxWidth()
            )
        }
    }
}