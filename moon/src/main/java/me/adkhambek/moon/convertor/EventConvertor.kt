package me.adkhambek.moon.convertor

import me.adkhambek.moon.Moon
import java.lang.reflect.Type

public fun interface EventConvertor<T, R> {

    public operator fun invoke(value: T): R

    public interface Factory {

        public fun fromEvent(
            type: Type,
            annotations: Array<Annotation>,
            moon: Moon,
        ): EventConvertor<String, *>?

        public fun toEvent(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            moon: Moon,
        ): EventConvertor<Any, String>?
    }
}
