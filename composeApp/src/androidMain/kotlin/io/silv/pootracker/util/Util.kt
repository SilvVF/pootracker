package io.silv.pootracker.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

actual fun uuid() = UUID.randomUUID().toString()


inline fun <T, V> Flow<Iterable<T>>.mapEach(
    crossinline block: (item: T) -> V
) = map { iterable ->
    iterable.map { item ->
        block(item)
    }
}