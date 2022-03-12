package me.adkhambek.moon.convertor

import me.adkhambek.moon.Moon
import java.lang.reflect.Type


interface EventConvertor<T, R> {

    operator fun invoke(value: T): R

    interface Factory {

        fun fromEvent(
            type: Type,
            annotations: Array<Annotation>,
            moon: Moon,
        ): EventConvertor<String, *>?

        fun toEvent(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            moon: Moon,
        ): EventConvertor<Any, String>?
    }
}

