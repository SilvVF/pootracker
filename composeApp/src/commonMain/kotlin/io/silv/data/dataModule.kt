package io.silv.data

import io.silv.database.Adapters
import io.silv.database.sqlDriverKoinDefinition
import io.silv.Database
import iosilvsqldelight.PoopLog
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
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

    factoryOf(::GetPoopLogs)

    factoryOf(::PoopLogHandler)
}