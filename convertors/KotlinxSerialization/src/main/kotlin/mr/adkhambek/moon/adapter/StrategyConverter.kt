package mr.adkhambek.moon.adapter

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import okhttp3.MediaType


@ExperimentalSerializationApi
internal class DeserializationStrategyConverter<T>(
    private val loader: DeserializationStrategy<T>,
    private val serializer: Serializer<String>
) : EventAdapter<String, T> {
    override fun convert(value: String) = serializer.fromEvent(loader, value)
}

@ExperimentalSerializationApi
internal class SerializationStrategyConverter<T>(
    private val contentType: MediaType,
    private val saver: SerializationStrategy<T>,
    private val serializer: Serializer<String>
) : EventAdapter<T, String> {
    override fun convert(value: T) = serializer.toEvent(contentType, saver, value)
}