package io.silv.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import app.cash.molecule.RecompositionMode
import app.cash.molecule.RecompositionMode.*
import app.cash.molecule.launchMolecule
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.concurrent.PlatformMainDispatcher
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

expect abstract class MoleculeScreenModel<Event, Model>() : ScreenModel {

    val models: StateFlow<Model>

    fun take(event: Event)

    @Composable
    protected abstract fun models(events: Flow<Event>): Model
}