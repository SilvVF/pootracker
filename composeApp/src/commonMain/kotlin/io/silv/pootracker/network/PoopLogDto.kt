package io.silv.pootracker.network

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoopLogDto(

    val id: String,

    @SerialName("created_by")
    val createdBy: String,

    @SerialName("created_at")
    val createdAt: Instant,

    val deleted: Boolean,

    @SerialName("cord_x")
    val cordX: Double?,

    @SerialName("cord_y")
    val cordY: Double?
)