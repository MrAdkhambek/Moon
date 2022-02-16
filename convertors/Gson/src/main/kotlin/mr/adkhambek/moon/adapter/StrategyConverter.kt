package mr.adkhambek.moon.adapter

import com.google.gson.TypeAdapter


internal class DeserializationStrategyConverter<T>(
    private val adapter: TypeAdapter<T>
) : EventAdapter<String, T> {

    override fun convert(value: String): T = adapter.fromJson(value)
}

internal class SerializationStrategyConverter<T>(
    private val adapter: TypeAdapter<T>
) : EventAdapter<T, String> {

    override fun convert(value: T): String = adapter.toJson(value)
}