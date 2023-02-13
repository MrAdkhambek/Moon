package com.adkhambek.moon.convertor

import com.adkhambek.moon.provider.BodyConverterProvider
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Suppress("NOTHING_TO_INLINE")
public inline fun Gson.asConverterFactory(): EventConvertor.Factory {
    return GsonAdapterFactory(this)
}

public class GsonAdapterFactory constructor(
    private val gson: Gson
) : EventConvertor.Factory {

    public constructor() : this(Gson())

    override fun fromEvent(
        type: Type,
        annotations: Array<Annotation>,
        converterProvider: BodyConverterProvider,
    ): EventConvertor<String, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return DeserializationStrategyConverter(adapter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun toEvent(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        converterProvider: BodyConverterProvider,
    ): EventConvertor<Any, String> {
        val adapter: TypeAdapter<out Any> = gson.getAdapter(TypeToken.get(type))
        return SerializationStrategyConverter(adapter as TypeAdapter<Any>)
    }
}
