@file:JvmName("KotlinSerializationConverterFactory")

package me.adkhambek.moon.convertor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import okhttp3.MediaType
import java.lang.reflect.Type


@ExperimentalSerializationApi
internal class Factory(
    private val contentType: MediaType,
    private val serializer: Serializer<String>
) : EventConvertor.Factory {

    override fun fromEvent(type: Type, annotations: Array<Annotation>): EventConvertor<String, Any> {
        val loader: KSerializer<Any> = serializer.serializer(type)
        return DeserializationStrategyConverter(loader, serializer)
    }

    override fun toEvent(type: Type, annotations: Array<Annotation>): EventConvertor<Any, String> {
        val saver = serializer.serializer(type)
        return SerializationStrategyConverter(contentType, saver, serializer)
    }
}

@ExperimentalSerializationApi
@JvmName("create")
fun StringFormat.asConverterFactory(contentType: MediaType): EventConvertor.Factory {
    return Factory(contentType, Serializer.FromString(this))
}
