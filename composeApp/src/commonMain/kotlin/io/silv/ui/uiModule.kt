package io.silv.ui

import io.silv.ui.logs.LogsScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val uiModule = module {

    factoryOf(::LogsScreenModel)
}