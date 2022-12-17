@file:Suppress("NOTHING_TO_INLINE")

package me.adkhambek.moon

import io.socket.client.IO
import io.socket.client.IO.Options
import me.adkhambek.moon.convertor.EventConvertor
import java.net.URI

public inline fun Moon.Companion.factory(): Moon.Factory {
    return Moon.Factory()
}

public fun Moon.Factory.create(
    uri: String,
    logger: Logger,
    vararg converterFactories: EventConvertor.Factory,
): Moon = create(
    socket = IO.socket(uri),
    logger = logger,
    *converterFactories
)

public fun Moon.Factory.create(
    uri: String,
    opts: Options,
    logger: Logger,
    vararg converterFactories: EventConvertor.Factory,
): Moon = create(
    socket = IO.socket(uri, opts),
    logger = logger,
    *converterFactories
)

public fun Moon.Factory.create(
    uri: URI,
    logger: Logger,
    vararg converterFactories: EventConvertor.Factory,
): Moon = create(
    socket = IO.socket(uri),
    logger = logger,
    *converterFactories
)

public fun Moon.Factory.create(
    uri: URI,
    opts: Options,
    logger: Logger,
    vararg converterFactories: EventConvertor.Factory,
): Moon = create(
    socket = IO.socket(uri, opts),
    logger = logger,
    *converterFactories
)