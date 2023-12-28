package io.silv.data

import io.silv.models.GeoPoint
import io.silv.Database
import iosilvsqldelight.PoopLog
import kotlinx.coroutines.withContext
import io.silv.util.IODispatcher
import iosilvsqldelight.User
import kotlinx.datetime.Instant

class PoopLogHandler(
    private val database: Database,
    private val ioDispatcher: IODispatcher
) {

    suspend fun insert(
        logId: String,
        instant: Instant,
        location: GeoPoint,
        createdBy: String
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

