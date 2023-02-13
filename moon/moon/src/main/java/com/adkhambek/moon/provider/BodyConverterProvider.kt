package com.adkhambek.moon.provider

import com.adkhambek.moon.convertor.EventConvertor
import java.lang.reflect.Type

public interface BodyConverterProvider {

    // ///////////////////////////////////////////
    // Response
    // ///////////////////////////////////////////
    public fun <T> responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T>

    public fun <T> nextResponseBodyConverter(
        skipPast: EventConvertor.Factory?,
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T>

    // ///////////////////////////////////////////
    // Request
    // ///////////////////////////////////////////
    public fun <T> requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
    ): EventConvertor<T, String>

    public fun <T> nextRequestBodyConverter(
        skipPast: EventConvertor.Factory?,
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
    ): EventConvertor<T, String>
}
