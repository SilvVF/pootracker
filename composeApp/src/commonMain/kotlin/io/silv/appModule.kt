package io.silv

import io.silv.data.dataModule
import io.silv.network.networkModule
import io.silv.ui.uiModule
import org.koin.dsl.module
import io.silv.util.AppDispatchers

val appModule = module {
    includes(dataModule, uiModule, networkModule)

    single {
        AppDispatchers.create()
    }

    single { get<AppDispatchers>().io }
    single { get<AppDispatchers>().default }
    single { get<AppDispatchers>().unconfinedDispatcher }
    single { get<AppDispatchers>().main }
}