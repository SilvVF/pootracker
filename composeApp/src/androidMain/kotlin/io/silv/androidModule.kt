package io.silv

import io.silv.sync.AndroidSyncWorker
import io.silv.util.NetworkConnectivity
import io.silv.util.NetworkConnectivityImpl
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val androidModule = module {

    factoryOf(::NetworkConnectivityImpl) withOptions {
        bind<NetworkConnectivity>()
    }

    workerOf(::AndroidSyncWorker)
}