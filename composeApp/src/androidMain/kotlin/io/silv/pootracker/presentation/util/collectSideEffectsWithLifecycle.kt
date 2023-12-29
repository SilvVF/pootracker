package io.silv.pootracker.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.silv.pootracker.util.MoleculeEffectScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("composablenaming")
@Composable
fun <Effect> MoleculeEffectScreenModel<Effect, *>.collectSideEffectsWithLifecycle(block: suspend (effect: Effect) -> Unit) {

    val effectsFlow = this.events
    val lifecycleOwner = LocalLifecycleOwner.current


    LaunchedEffect(lifecycleOwner) {

        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main.immediate) {
                    effectsFlow.collect {
                        block(it)
                    }
                }
            }
        }
    }
}