package io.silv.pootracker

import io.silv.pootracker.presentation.uiModule
import io.silv.pootracker.sync.AndroidSyncWorker
import io.silv.pootracker.util.NetworkConnectivity
import io.silv.pootracker.util.NetworkConnectivityImpl
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val androidModule = module {

//    singleOf(::AndroidDatabaseHandler) {
//        bind<DatabaseHandler>()
//    }

    includes(uiModule)

    factoryOf(::NetworkConnectivityImpl) withOptions {
        bind<NetworkConnectivity>()
    }

    workerOf(::AndroidSyncWorker)
}