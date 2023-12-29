package io.silv.pootracker.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.Google
import io.silv.pootracker.util.MoleculeScreenModel
import kotlinx.coroutines.launch

class AuthScreenModel(
    private val auth: Auth,
    private val composeAuth: ComposeAuth,
): MoleculeScreenModel<AuthState>() {

    @Composable
    override fun models(): AuthState {
        return authPresenter(auth, composeAuth)
    }
}

sealed interface AuthAction {
    data class EmailChange(val email: String): AuthAction
    data class PasswordChange(val password: String): AuthAction
    data class PhoneChange(val phone: String): AuthAction
}

data class AuthState(
    val composeAuth: ComposeAuth,
    val auth: Auth,
    val password: String,
    val email: String,
    val phone: String,
    val actions: (AuthAction) -> Unit
) {

    val emailMandatory = email.isNotBlank()
}


@Composable
private fun authPresenter(
    auth: Auth,
    composeAuth: ComposeAuth,
): AuthState {

    val scope = rememberCoroutineScope()

    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val authState = auth.sessionStatus.collectAsState().value

    fun loginOtp(email: String) {
        scope.launch {
            runCatching {
                auth.signInWith(Google)
            }
        }
    }

    return AuthState(
        password = password,
        email = email,
        phone = phone,
        composeAuth = composeAuth,
        auth = auth
    ) { action ->
        when (action) {
            is AuthAction.EmailChange -> { email = action.email }
            is AuthAction.PasswordChange -> { password = action.password }
            is AuthAction.PhoneChange -> { phone = action.phone }
        }
     }
}