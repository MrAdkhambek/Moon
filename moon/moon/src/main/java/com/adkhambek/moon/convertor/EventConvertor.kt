package com.adkhambek.moon.convertor

import com.adkhambek.moon.provider.BodyConverterProvider
import java.lang.reflect.Type

public fun interface EventConvertor<T, R> {

    public operator fun invoke(value: T): R

    public interface Factory {

        public fun fromEvent(
            type: Type,
            annotations: Array<Annotation>,
            converterProvider: BodyConverterProvider,
        ): EventConvertor<String, *>?

        public fun toEvent(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            converterProvider: BodyConverterProvider,
        ): EventConvertor<Any, String>?
    }
}
