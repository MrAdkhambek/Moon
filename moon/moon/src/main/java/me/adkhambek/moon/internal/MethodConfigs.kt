@file:[JvmName("MethodConfigs") JvmSynthetic]
package me.adkhambek.moon.internal

import me.adkhambek.moon.Event
import me.adkhambek.moon.Utils
import java.lang.reflect.Method
import java.lang.reflect.Type

internal class MethodConfigs @JvmOverloads constructor(
    val method: Method,
    val returnType: Type,
    val methodAnnotations: Array<Annotation> = method.annotations,
    val parameterTypes: Array<Type> = method.genericParameterTypes,
    val parameterAnnotationsArray: Array<out Array<Annotation>> = method.parameterAnnotations,
) {

    val socketEvent: String
        get() {
            val annotation = methodAnnotations
                .asSequence()
                .filterIsInstance<Event>()
                .firstOrNull()
                ?: throw Utils.methodError(method, NEED_EVENT_ANNOTATION)

            return annotation.value
        }
}
