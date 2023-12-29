package io.silv.pootracker.ui

import io.silv.pootracker.ui.auth.AuthScreenModel
import io.silv.pootracker.ui.logs.LogsScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val uiModule = module {

    factoryOf(::LogsScreenModel)

    factoryOf(::AuthScreenModel)
}