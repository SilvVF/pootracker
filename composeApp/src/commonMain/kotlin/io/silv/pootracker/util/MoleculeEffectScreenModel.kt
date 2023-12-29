package io.silv.pootracker.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

/**
 * Implements [launchMolecule] that exposes a flow of type [Model] to be consumed by the ui.
 * [SendChannel] of [Effect] is exposed to the models function for presenters to send side effects.
 * @param events received the presenters side effect channel as a Flow.
 */
abstract class MoleculeEffectScreenModel<Effect, Model>: ScreenModel {

    private val scope = CoroutineScope(screenModelScope.coroutineContext)

    private val mutableEvents = Channel<Effect>(UNLIMITED)
    val events = mutableEvents.receiveAsFlow()

    val models: StateFlow<Model> by lazy(LazyThreadSafetyMode.NONE) {
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            models(mutableEvents)
        }
    }

    @Composable
    protected abstract fun models(events: SendChannel<Effect>): Model
}

@Composable
fun <Effect> MoleculeEffectScreenModel<Effect, *>.sideEffects(
    onReceived: suspend (effect: Effect) -> Unit
) {
    val sideEffectFlow = events

    LaunchedEffect(sideEffectFlow) {
        withContext(Dispatchers.Main.immediate) {
            sideEffectFlow.collect { onReceived(it) }
        }
    }
}

abstract class MoleculeScreenModel<Model> : ScreenModel {

    private val scope = CoroutineScope(screenModelScope.coroutineContext)

    val models: StateFlow<Model> by lazy(LazyThreadSafetyMode.NONE) {
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            models()
        }
    }

    @Composable
    protected abstract fun models(): Model
}