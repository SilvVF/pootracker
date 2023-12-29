package io.silv.pootracker.database

import app.cash.sqldelight.ColumnAdapter
import io.silv.pootracker.domain.models.GeoPoint
import kotlinx.datetime.Instant

object Adapters {

    fun instantToLong() = object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long): Instant {
            return Instant.fromEpochMilliseconds(databaseValue)
        }

        override fun encode(value: Instant): Long {
            return value.toEpochMilliseconds()
        }
    }

    fun geoPointToString() = object : ColumnAdapter<GeoPoint, String> {

        private val delimiter = '|'

        override fun decode(databaseValue: String): GeoPoint {
           val dbValues = databaseValue.split(delimiter)
           return GeoPoint(
               dbValues.first().toDouble(),
               dbValues.last().toDouble()
           )
        }
        override fun encode(value: GeoPoint): String {
            return "${value.x}$delimiter${value.y}"
        }
    }
}