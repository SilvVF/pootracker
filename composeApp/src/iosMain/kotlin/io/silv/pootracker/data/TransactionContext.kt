package io.silv.pootracker.data

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.identityHashCode

@OptIn(ExperimentalNativeApi::class)
actual fun identityHashCode(instance: Any?): Int = instance.identityHashCode()