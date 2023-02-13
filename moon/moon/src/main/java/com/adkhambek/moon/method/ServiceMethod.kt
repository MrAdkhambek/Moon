package com.adkhambek.moon.method

import com.adkhambek.moon.internal.MethodConfigs
import com.adkhambek.moon.Utils.methodError
import com.adkhambek.moon.convertor.EventConvertor
import com.adkhambek.moon.provider.BodyConverterProvider
import java.lang.reflect.Type

internal abstract class ServiceMethod<T>(
    protected val converterProvider: BodyConverterProvider,
    protected val methodConfigs: MethodConfigs
) {

    abstract fun invoke(args: Array<Any>): T

    protected fun <ResponseT> createResponseConverter(
        responseType: Type,
    ): EventConvertor<String, ResponseT> = try {
        val annotations = methodConfigs.methodAnnotations
        converterProvider.responseBodyConverter(responseType, annotations)
    } catch (e: RuntimeException) { // Wide exception range because factories are user code.
        throw methodError(methodConfigs.method, e, "Unable to create converter for %s", responseType)
    }

    protected fun <ReturnT> createRequestConverter(
        responseType: Type,
        parameterAnnotations: Array<Annotation>,
    ): EventConvertor<ReturnT, String> = try {
        val annotations = methodConfigs.methodAnnotations
        converterProvider.requestBodyConverter(responseType, parameterAnnotations, annotations)
    } catch (e: RuntimeException) { // Wide exception range because factories are user code.
        throw methodError(methodConfigs.method, e, "Unable to create converter for %s", responseType)
    }
}
