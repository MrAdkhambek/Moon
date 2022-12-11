package me.adkhambek.moon.method

import kotlinx.coroutines.flow.Flow
import me.adkhambek.moon.MethodConfigs
import me.adkhambek.moon.Moon
import me.adkhambek.moon.Utils
import me.adkhambek.moon.Utils.methodError
import me.adkhambek.moon.convertor.EventConvertor
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.coroutines.Continuation

internal abstract class ServiceMethod<T>(
    protected val moon: Moon,
    protected val methodConfigs: MethodConfigs
) {

    abstract fun invoke(args: Array<Any>): T

    protected fun <ResponseT> createResponseConverter(
        responseType: Type,
    ): EventConvertor<String, ResponseT> {
        val annotations = methodConfigs.methodAnnotations

        return try {
            moon.responseBodyConverter(responseType, annotations)
        } catch (e: RuntimeException) { // Wide exception range because factories are user code.
            throw methodError(methodConfigs.method, e, "Unable to create converter for %s", responseType)
        }
    }

    protected fun <ReturnT> createRequestConverter(
        responseType: Type,
        parameterAnnotations: Array<Annotation>,
    ): EventConvertor<ReturnT, String> {
        val annotations = methodConfigs.methodAnnotations

        return try {
            moon.requestBodyConverter(responseType, parameterAnnotations, annotations)
        } catch (e: RuntimeException) { // Wide exception range because factories are user code.
            throw methodError(methodConfigs.method, e, "Unable to create converter for %s", responseType)
        }
    }

    companion object {

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> parseAnnotations(
            moon: Moon,
            method: Method,
        ): ServiceMethod<T> {

            val returnType = method.genericReturnType
            val methodConfigs = MethodConfigs(method, returnType)

            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                    method, "Method return type must not include a type variable or wildcard: %s", returnType
                )
            }

            if (returnType === Void.TYPE) {
                throw methodError(method, "Service methods cannot return void.")
            }

            if (Utils.getRawType(returnType) == Flow::class.java) {
                return CoroutinesMethods(methodConfigs, moon) as ServiceMethod<T>
            }

            val lastItemType = method.genericParameterTypes.lastOrNull()
            if (lastItemType != null && Utils.getRawType(lastItemType) == Continuation::class.java) {
                return CoroutinesMethods(methodConfigs, moon) as ServiceMethod<T>
            }

            throw RuntimeException("Can't support this method")
        }
    }
}
