package io.silv.pootracker.presentation

import io.silv.pootracker.presentation.auth.AuthScreenModel
import io.silv.pootracker.presentation.logs.LogsScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val uiModule = module {

    factoryOf(::LogsScreenModel)

    factoryOf(::AuthScreenModel)
}