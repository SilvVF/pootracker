package io.silv.models

import iosilvsqldelight.PoopLog
import kotlinx.datetime.Instant

data class DomainPoopLog(
    val id: Long,
    val logId: String,
    val createdBy: String,
    val instant: Instant
) {

    constructor(poopLog: PoopLog): this(
        id = poopLog.id,
        logId = poopLog.logId,
        createdBy = poopLog.createdBy,
        instant = poopLog.instant
    )
}