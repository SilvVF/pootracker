package io.silv.util

import platform.Foundation.NSUUID

actual fun uuid() = NSUUID.UUID().UUIDString()
