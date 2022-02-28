@file:JvmName("EventFactory")

package me.adkhambek.moon

import io.socket.client.Socket
import java.lang.reflect.Method
import java.lang.reflect.Type


internal class EventFactory @JvmOverloads constructor(
    val socketIO: Socket,
    val method: Method,
    val returnType: Type,
    val methodAnnotations: Array<out Annotation> = method.annotations,
    val parameterTypes: Array<out Type> = method.genericParameterTypes,
    val parameterAnnotationsArray: Array<out Array<Annotation>> = method.parameterAnnotations,
) {

    companion object {

        @JvmStatic
        fun getSocketEvent(method: Method): String {
            val annotation = method.annotations.firstOrNull { it is Event }
                ?: throw Utils.methodError(method, "Method must at least one Event annotation")
            return (annotation as Event).value
        }
    }
}