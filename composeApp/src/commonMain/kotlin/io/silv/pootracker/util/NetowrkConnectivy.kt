package io.silv.pootracker.util

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivity {
    val online: Flow<Boolean>
}