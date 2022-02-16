package mr.adkhambek.moon.adapter

import java.lang.reflect.Type


interface EventAdapter<T, R> {

    fun convert(value: T): R

    interface Factory {
        fun fromEvent(type: Type, annotations: Array<Annotation>): EventAdapter<String, *>
        fun toEvent(type: Type, annotations: Array<Annotation>): EventAdapter<Any, String>
    }
}

