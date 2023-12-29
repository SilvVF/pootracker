package io.silv.pootracker.data

import io.silv.Database
import io.silv.pootracker.models.GeoPoint
import io.silv.pootracker.util.IODispatcher
import iosilvsqldelight.PoopLog
import iosilvsqldelight.User
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class PoopLogHandler(
    private val database: Database,
    private val ioDispatcher: IODispatcher
) {

    suspend fun insert(
        logId: String,
        instant: Instant,
        createdBy: String,
        location: GeoPoint? = null
    ) = withContext(ioDispatcher) {
        runCatching {

            database.userQueries.insert(
                User(createdBy)
            )

            database.poopLogQueries.insert(
                PoopLog(logId, createdBy, instant, location, false, false)
            )
        }
    }
}

