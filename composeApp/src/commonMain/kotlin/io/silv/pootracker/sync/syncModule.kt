package io.silv.pootracker.sync

import com.russhwolf.settings.Settings
import org.koin.dsl.module

val syncModule = module {

    single { SyncManager(get(), get()) }

    single { SyncStore(get(), get()) }

    single { Settings() }
}