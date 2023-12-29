package io.silv.pootracker.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.silv.pootracker.data.DB_NAME
import io.silv.Database
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual class DriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            Database.Schema,
            context,
            DB_NAME,
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}

actual fun Module.sqlDriverKoinDefinition() = single {
    DriverFactory(androidContext()).createDriver()
}