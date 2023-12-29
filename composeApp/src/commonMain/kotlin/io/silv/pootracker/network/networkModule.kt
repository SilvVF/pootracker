package io.silv.pootracker.network

import GOOGLE_WEB_CLIENT_ID
import SUPABASE_KEY
import SUPABASE_URL
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {

    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    single {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY,
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
            }
            install(Postgrest) {
                serializer = KotlinXSerializer(get())
            }
            install(ComposeAuth) {
                googleNativeLogin(
                    isSupported = true,
                    serverClientId = GOOGLE_WEB_CLIENT_ID
                )
            }
        }
    }

    single {
        get<SupabaseClient>().composeAuth
    }

    single {
        get<SupabaseClient>().auth
    }

    single{
        get<SupabaseClient>().postgrest
    }

    single {
        SupabaseApi(get(), get())
    }
}