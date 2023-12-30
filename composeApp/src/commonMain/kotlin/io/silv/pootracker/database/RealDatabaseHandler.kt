package io.silv.pootracker.database

import app.cash.sqldelight.db.SqlDriver
import io.silv.Database
import io.silv.pootracker.data.DatabaseHandler
import io.silv.pootracker.util.IODispatcher

expect class RealDatabaseHandler(): DatabaseHandler {
    val db: Database
    val driver: SqlDriver
    val queryDispatcher: IODispatcher
    val transactionDispatcher: IODispatcher
}