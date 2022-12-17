@file:[Suppress("UNUSED_PARAMETER") JvmName("ThrowX") JvmSynthetic]

package me.adkhambek.moon.internal

import io.socket.client.Socket
import me.adkhambek.moon.MoonConnectException

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
