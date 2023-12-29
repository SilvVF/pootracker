package io.silv.pootracker.models

import io.silv.pootracker.network.PoopLogDto
import iosilvsqldelight.PoopLog
import kotlinx.datetime.Instant

data class DomainPoopLog(
    val id: String,
    val createdBy: String,
    val createdAt: Instant,
    val location: GeoPoint?,
    val synced: Boolean,
) {

    constructor(resource: PoopLog): this(
        id = resource.id,
        createdBy = resource.createdBy,
        createdAt = resource.instant,
        location = resource.location,
        synced = false,
    )

    constructor(dto: PoopLogDto): this(
        id = dto.id,
        createdBy = dto.createdBy,
        createdAt = dto.createdAt,
        location = if (dto.cordX != null && dto.cordY != null) { GeoPoint(dto.cordX, dto.cordY) } else null,
        synced = true,
    )
}