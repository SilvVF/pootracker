package io.silv.network

import SUPABASE_KEY
import SUPABASE_URL
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
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
            install(Postgrest) {
                serializer = KotlinXSerializer(get())
            }

        }
    }

    single {
        SupabaseApi(get(), get())
    }
}