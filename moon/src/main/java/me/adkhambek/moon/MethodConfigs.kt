@file:JvmName("EventFactory")

package me.adkhambek.moon

import java.lang.reflect.Method
import java.lang.reflect.Type


internal class MethodConfigs @JvmOverloads constructor(
    val method: Method,
    val returnType: Type,
    val methodAnnotations: Array<Annotation> = method.annotations,
    val parameterTypes: Array<Type> = method.genericParameterTypes,
    val parameterAnnotationsArray: Array<out Array<Annotation>> = method.parameterAnnotations,
) {

    fun getSocketEvent(): String {
        val annotation = method
            .annotations
            .firstOrNull { it is Event }
            ?: throw Utils.methodError(
                method,
                "Method must at least one Event annotation"
            )

        return (annotation as Event).value
    }
}