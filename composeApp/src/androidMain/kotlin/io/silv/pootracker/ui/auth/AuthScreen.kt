@file:OptIn(SupabaseExperimental::class)

package io.silv.pootracker.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.ui.AuthForm
import io.github.jan.supabase.compose.auth.ui.FormComponent
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import io.github.jan.supabase.compose.auth.ui.password.rememberPasswordRuleList
import io.github.jan.supabase.compose.auth.ui.phone.OutlinedPhoneField
import io.github.jan.supabase.gotrue.providers.Google
import kotlinx.coroutines.launch

class AuthScreen: Screen {
    
    @OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        
        val screenModel = getScreenModel<AuthScreenModel>()
        
        val models by screenModel.models.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow

        AuthScreenContent(
            models,
            googleLoginSuccess = {
                navigator.pop()
            },
            goBack = {
                navigator.pop()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreenContent(
    models: AuthState,
    googleLoginSuccess: () -> Unit,
    goBack: () -> Unit
) {
    val actions = models.actions
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showSnackbar(message: String) {
        scope.launch {
            snackBarHostState.showSnackbar(message)
        }
    }

    val googleSignIn = models.composeAuth.rememberSignInWithGoogle(
        onResult = { result -> //optional error handling
            when (result) {
                is NativeSignInResult.Success -> {
                    googleLoginSuccess()
                }
                is NativeSignInResult.ClosedByUser -> {

                }
                is NativeSignInResult.Error -> {
                    showSnackbar(result.message)
                }
                is NativeSignInResult.NetworkError -> {
                    showSnackbar(result.message)
                }
            }
        },
        fallback = { // optional: add custom error handling, not required by default
            runCatching {
                models.auth.signInWith(Google)
            }.onFailure {
                it.printStackTrace()
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        AuthForm {
            val state = LocalAuthState.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedEmailField(
                    value = models.email,
                    onValueChange = { actions(AuthAction.EmailChange(it)) },
                    label = { Text("E-Mail") },
                    mandatory = models.emailMandatory //once an email is entered, it is mandatory. (which enable validation)
                )
                OutlinedPhoneField(
                    value = models.phone,
                    onValueChange = { actions(AuthAction.PhoneChange(it)) },
                    label = { Text("Phone Number") }
                )
                OutlinedPasswordField(
                    value = models.password,
                    onValueChange = { actions(AuthAction.PasswordChange(it)) },
                    label = { Text("Password") },
                    rules = rememberPasswordRuleList(
                        PasswordRule.minLength(6),
                        PasswordRule.containsSpecialCharacter(),
                        PasswordRule.containsDigit(),
                        PasswordRule.containsLowercase(),
                        PasswordRule.containsUppercase()
                    )
                )
                FormComponent("accept_terms") { valid ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = valid.value,
                            onCheckedChange = { valid.value = it },
                        )
                        Text("Accept Terms", color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Button(
                    onClick = {}, //Login with email and password,
                    enabled = state.validForm,
                ) {
                    Text("Login")
                }
                OutlinedButton(
                    onClick = {
                        googleSignIn.startFlow()
                              }, //Login with Google,
                    content = { ProviderButtonContent(Google) }
                )
            }
        }
    }
}