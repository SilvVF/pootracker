package io.silv.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoopLogDto(
    val id: String,

    @SerialName("created_at")
    val createdAt: Long,
)