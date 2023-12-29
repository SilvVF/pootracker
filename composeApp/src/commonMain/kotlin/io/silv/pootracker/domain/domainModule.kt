package io.silv.pootracker.domain

import io.silv.pootracker.domain.logs.interactor.GetLog
import io.silv.pootracker.domain.logs.interactor.GetLogs
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::GetLog)
    factoryOf(::GetLogs)

}