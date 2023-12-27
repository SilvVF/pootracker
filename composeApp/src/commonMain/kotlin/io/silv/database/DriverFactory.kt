package io.silv.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module



expect class DriverFactory {
    fun createDriver(): SqlDriver
}

expect fun Module.sqlDriverKoinDefinition(): KoinDefinition<SqlDriver>