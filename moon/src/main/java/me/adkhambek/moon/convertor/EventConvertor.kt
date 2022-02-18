package me.adkhambek.moon.convertor

import java.lang.reflect.Type


interface EventConvertor<T, R> {

    operator fun invoke(value: T): R

    interface Factory {
        fun fromEvent(type: Type, annotations: Array<Annotation>): EventConvertor<String, *>
        fun toEvent(type: Type, annotations: Array<Annotation>): EventConvertor<Any, String>
    }
}

