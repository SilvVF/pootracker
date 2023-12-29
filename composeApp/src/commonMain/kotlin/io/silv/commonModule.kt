package io.silv

import io.silv.pootracker.data.dataModule
import io.silv.pootracker.domain.domainModule
import io.silv.pootracker.network.networkModule
import io.silv.pootracker.sync.syncModule
import io.silv.pootracker.util.AppDispatchers
import io.silv.pootracker.util.ApplicationScope
import org.koin.dsl.module


val commonModule = module {

    includes(dataModule, networkModule, syncModule, domainModule)

    single {
        ApplicationScope()
    }

    single {
        AppDispatchers.create()
    }

    single { get<AppDispatchers>().io }
    single { get<AppDispatchers>().default }
    single { get<AppDispatchers>().unconfinedDispatcher }
    single { get<AppDispatchers>().main }
}