package me.adkhambek.moon.convertor

import com.google.gson.TypeAdapter


internal class DeserializationStrategyConverter<T>(
    private val adapter: TypeAdapter<T>
) : EventConvertor<String, T> {

    override operator fun invoke(value: String): T = adapter.fromJson(value)
}

internal class SerializationStrategyConverter<T>(
    private val adapter: TypeAdapter<T>
) : EventConvertor<T, String> {

    override operator fun invoke(value: T): String = adapter.toJson(value)
}