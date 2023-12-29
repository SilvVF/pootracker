@file:OptIn(ExperimentalMaterial3Api::class)

package io.silv.pootracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.silv.pootracker.ui.logs.LogsScreen
import io.silv.pootracker.ui.util.PooTrackerTheme
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val supabase by inject<SupabaseClient>()

    private var onSessionSuccess: (() -> Unit)? = null

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        supabase.handleDeeplinks(intent) {
            onSessionSuccess?.invoke()
        }

        setContent {
            PooTrackerTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0),
                ) { incoming ->
                    Navigator(screen = LogsScreen()) {
                        Box(
                            Modifier
                                .padding(incoming)
                                .consumeWindowInsets(incoming),
                        ) {

                            LaunchedEffect(Unit) {
                                onSessionSuccess = {
                                    it.replaceAll(LogsScreen())
                                }
                            }

                            FadeTransition(it)
                        }
                    }
                }
            }
        }
    }
}
