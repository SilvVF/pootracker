package io.silv.pootracker.data.logs

import io.silv.pootracker.domain.logs.model.Log
import io.silv.pootracker.domain.models.GeoPoint
import kotlinx.datetime.Instant

object LogsMapper {

    fun mapLog (
        id: Long,
        log_id: String,
        createdBy: String,
        instant: Instant,
        location: GeoPoint?,
        synced: Boolean
    ): Log = Log(
        id = id,
        logId = log_id,
        createdBy = createdBy,
        createdAt = instant,
        location = location,
        synced = synced
    )
}