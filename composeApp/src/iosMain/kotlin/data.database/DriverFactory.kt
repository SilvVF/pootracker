package io.silv.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import io.silv.data.DB_NAME
import io.silv.Database
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            Database.Schema,
            DB_NAME,
            onConfiguration = { config ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        foreignKeyConstraints = true
                    )
                )
            }
        )
    }
}

actual fun Module.sqlDriverKoinDefinition(): KoinDefinition<SqlDriver> {
    return single {
        DriverFactory().createDriver()
    }
}