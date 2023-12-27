package io.silv.util

import androidx.compose.runtime.Composable
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

actual abstract class MoleculeScreenModel<Event, Model> : ScreenModel {

    private val scope = CoroutineScope(screenModelScope.coroutineContext)
    // Events have a capacity large enough to handle simultaneous UI events, but
    // small enough to surface issues if they get backed up for some reason.
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

    actual val models: StateFlow<Model> by lazy(LazyThreadSafetyMode.NONE) {
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            models(events)
        }
    }

    actual fun take(event: Event) {
        if (!events.tryEmit(event)) {
            error("Event buffer overflow.")
        }
    }

    @Composable
    protected actual abstract fun models(events: Flow<Event>): Model
}