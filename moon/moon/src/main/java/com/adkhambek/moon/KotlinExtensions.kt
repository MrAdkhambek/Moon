@file:JvmName("KotlinExtensions")
@file:Suppress("UNCHECKED_CAST")

package com.adkhambek.moon

import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import com.adkhambek.moon.convertor.EventConvertor
import com.adkhambek.moon.internal.ARGUMENT_RESULT
import com.adkhambek.moon.internal.OFF
import com.adkhambek.moon.internal.ON
import com.adkhambek.moon.internal.REQUEST_WITHOUT_RESULT
import com.adkhambek.moon.internal.RESPONSE_RESULT
import com.adkhambek.moon.internal.STRING_WRITER_BUFFER_SIZE
import com.adkhambek.moon.internal.ThrowX.check
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.Nonnull
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Nonnull
internal suspend fun emitWithoutResponse(
    socket: Socket,
    event: String,
    logger: Logger,
    vararg args: Any
) {
    check(socket, event)
    return suspendCancellableCoroutine { continuation ->
        socket.emit(event, *args)
        continuation.resume(Unit)
        logger.log(event, REQUEST_WITHOUT_RESULT)
    }
}

@Nonnull
internal suspend fun <T> emitWithResponse(
    socket: Socket,
    event: String,
    logger: Logger,
    responseConvertor: EventConvertor<String, *>,
    args: Array<Any>
): T {
    check(socket, event)
    return suspendCancellableCoroutine { continuation ->
        val listener = Ack { arrayOfAny ->
            try {
                val firstArg = arrayOfAny.first().toString()
                logger.log(event, RESPONSE_RESULT.format(0, firstArg))
                val result: T = responseConvertor(firstArg) as T
                continuation.resume(result)
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }

        val emitter = socket.emit(event, args, listener)
        continuation.invokeOnCancellation {
            emitter.off()
            logger.log(event, OFF)
        }
    }
}

@Nonnull
internal fun flowResponse(
    socket: Socket,
    event: String,
    logger: Logger,
    responseConvertor: EventConvertor<String, *>,
): Flow<Any> = callbackFlow {

    val listener = Emitter.Listener { arrayOfAny ->
        arrayOfAny.forEachIndexed { index, any ->
            try {
                logger.log(event, ARGUMENT_RESULT.format(index, any.toString()))
                val arg: Any = requireNotNull(responseConvertor(any.toString()))
                logger.log(event, RESPONSE_RESULT.format(index, arg.toString()))
                require(trySend(arg).isSuccess)
            } catch (t: Throwable) {
                logger.e(event, t)
            }
        }
    }

    logger.log(event, ON)
    val emitter = socket.on(event, listener)
    awaitClose {
        logger.log(event, OFF)
        emitter.off(event, listener)
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

internal fun getStackTraceString(t: Throwable): String {
    // Don't replace this with Log.getStackTraceString() - it hides
    // UnknownHostException, which is not what we want.
    val sw = StringWriter(STRING_WRITER_BUFFER_SIZE)
    val pw = PrintWriter(sw, false)
    t.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}
