package io.silv.pootracker.data

import io.silv.Database
import io.silv.pootracker.database.Adapters
import io.silv.pootracker.database.sqlDriverKoinDefinition
import iosilvsqldelight.PoopLog
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

const val DB_NAME = "pootracker.db"

val dataModule = module {

    sqlDriverKoinDefinition()

    single {
        Database(
            driver = get(),
            PoopLogAdapter = PoopLog.Adapter(
                locationAdapter = Adapters.geoPointToString(),
                instantAdapter = Adapters.instantToLong()
            )
        )
    }

    factoryOf(::GetPoopLog)

    factoryOf(::GetPoopLogs)

    factoryOf(::PoopLogHandler)
}