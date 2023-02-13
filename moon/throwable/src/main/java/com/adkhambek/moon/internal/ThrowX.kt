@file:[Suppress("UNUSED_PARAMETER") JvmName("ThrowX") JvmSynthetic]

package com.adkhambek.moon.internal

import com.adkhambek.moon.MoonConnectException
import io.socket.client.Socket

internal object ThrowX {
    public fun check(
        socket: Socket,
        event: String,
    ) {
        if (!socket.connected()) {
            throw MoonConnectException()
        }
    }
}
