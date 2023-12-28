package io.silv.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO

typealias IODispatcher = CoroutineDispatcher
typealias DefaultDispatcher = CoroutineDispatcher
typealias MainDispatcher = CoroutineDispatcher
typealias UnconfinedDispatcher = CoroutineDispatcher


interface AppDispatchers {
    val io: IODispatcher
    val default: DefaultDispatcher
    val main: MainDispatcher
    val unconfinedDispatcher: UnconfinedDispatcher

    companion object {
        fun create() = object: AppDispatchers {
            override val io: IODispatcher = Dispatchers.IO
            override val default: DefaultDispatcher = Dispatchers.Default
            override val main: MainDispatcher = Dispatchers.Main
            override val unconfinedDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
        }
    }
}

data class ApplicationScope @OptIn(DelicateCoroutinesApi::class) constructor(
    val scope: CoroutineScope = GlobalScope
): CoroutineScope by scope
