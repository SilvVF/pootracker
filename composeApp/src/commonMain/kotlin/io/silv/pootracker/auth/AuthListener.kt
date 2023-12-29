package io.silv.pootracker.auth

import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.silv.pootracker.util.DefaultDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class AuthListener(
    private val auth: Auth,
    private val dispatcher: DefaultDispatcher
) {

    fun subscribe(): StateFlow<SessionStatus> = auth.sessionStatus

    suspend fun await() = auth.sessionStatus.first()
}