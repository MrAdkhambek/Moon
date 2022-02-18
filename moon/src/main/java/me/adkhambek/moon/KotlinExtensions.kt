@file:JvmName("KotlinExtensions")
@file:Suppress("UNCHECKED_CAST")

package me.adkhambek.moon

import io.socket.client.Ack
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import me.adkhambek.moon.convertor.EventConvertor
import javax.annotation.Nonnull
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Nonnull
internal suspend fun <T> awaitResponse(
    event: String,
    requestFactory: EventFactory,
    adapterFactory: EventConvertor.Factory,
    vararg eventArguments: Any,
): T {
    return suspendCancellableCoroutine { continuation ->
        try {
            val adapter: EventConvertor<String, *> = adapterFactory.fromEvent(requestFactory.returnType, arrayOf())

            val listener = Ack { arrayOfAny ->
                val firstArg = arrayOfAny.first().toString()
                val result: T = adapter(firstArg) as T
                continuation.resume(result)
            }

            requestFactory.socketIO.emit(event, *eventArguments, listener)
        } catch (t: Throwable) {
            continuation.resumeWithException(t)
        }
    }
}

@Nonnull
internal fun flowResponse(
    event: String,
    eventFactory: EventFactory,
    adapterFactory: EventConvertor.Factory,
): Flow<Any> = callbackFlow {

    val socketIO = eventFactory.socketIO
    val adapter: EventConvertor<String, *> = adapterFactory.fromEvent(eventFactory.returnType, arrayOf())

    val listener = Emitter.Listener { arrayOfAny ->
        arrayOfAny.forEach {
            try {
                require(trySend(requireNotNull(adapter(it.toString()))).isSuccess)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    socketIO.on(event, listener)
    awaitClose {
        println("Close event $event")
        socketIO.off(event)
    }
}

@Nonnull
internal suspend fun Exception.suspendAndThrow(): Nothing {
    suspendCoroutineUninterceptedOrReturn<Nothing> { continuation ->
        Dispatchers.Default.dispatch(continuation.context) {
            continuation.intercepted().resumeWithException(this@suspendAndThrow)
        }
        COROUTINE_SUSPENDED
    }
}
