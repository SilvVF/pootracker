package io.silv.pootracker.domain.logs.model

import io.silv.pootracker.domain.models.GeoPoint
import kotlinx.datetime.Instant

data class Log(
    val id: Long,
    val logId: String,
    val createdBy: String,
    val createdAt: Instant,
    val location: GeoPoint?,
    val synced: Boolean,
)