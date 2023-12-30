package io.silv.pootracker.data

import io.silv.Database
import io.silv.pootracker.data.logs.LogsRepositoryImpl
import io.silv.pootracker.database.Adapters
import io.silv.pootracker.database.RealDatabaseHandler
import io.silv.pootracker.database.sqlDriverKoinDefinition
import iosilvsqldelight.Logs
import org.koin.dsl.module

const val DB_NAME = "pootracker.db"

val dataModule = module {

    sqlDriverKoinDefinition()

    single {
        Database(
            driver = get(),
            logsAdapter = Logs.Adapter(
                locationAdapter = Adapters.geoPointToString(),
                instantAdapter = Adapters.instantToLong()
            )
        )
    }

    single<DatabaseHandler> { RealDatabaseHandler() }

    single { LogsRepositoryImpl(get()) }
}