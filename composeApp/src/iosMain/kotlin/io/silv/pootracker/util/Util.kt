package io.silv.pootracker.util

import platform.Foundation.NSUUID

actual fun uuid() = NSUUID.UUID().UUIDString()
