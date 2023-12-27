package io.silv.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import io.silv.ui.logs.LogsScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(screen = LogsScreen()) {
            FadeTransition(it)
        }
    }
}