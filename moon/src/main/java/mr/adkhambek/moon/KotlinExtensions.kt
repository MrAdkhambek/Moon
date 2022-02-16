@file:JvmName("KotlinExtensions")

package mr.adkhambek.moon

import io.socket.client.Ack
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import mr.adkhambek.moon.adapter.EventAdapter
import javax.annotation.Nonnull
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Nonnull
suspend fun <T> awaitResponse(
    event: String,
    requestFactory: EventFactory,
    adapterFactory: EventAdapter.Factory,
    vararg eventArguments: Any,
): T {
    return suspendCancellableCoroutine { continuation ->
        try {
            val adapter: EventAdapter<String, *> = adapterFactory.fromEvent(requestFactory.returnType, arrayOf())

            val listener = Ack { arrayOfAny ->
                val firstArg = arrayOfAny.first().toString()
                val result: T? = adapter.convert(firstArg) as T?
                continuation.resume(requireNotNull(result))
            }

            requestFactory.socketIO.emit(event, *eventArguments, listener)
        } catch (t: Throwable) {
            continuation.resumeWithException(t)
        }
    }
}

@Nonnull
fun flowResponse(
    event: String,
    eventFactory: EventFactory,
    adapterFactory: EventAdapter.Factory,
): Flow<Any> = callbackFlow {

    val socketIO = eventFactory.socketIO
    val adapter: EventAdapter<String, *> = adapterFactory.fromEvent(eventFactory.returnType, arrayOf())

    val listener = Emitter.Listener { arrayOfAny ->
        arrayOfAny.forEach {
            trySend(requireNotNull(adapter.convert(it.toString())))
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
