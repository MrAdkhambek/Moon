package me.adkhambek.moon.convertor

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import okhttp3.MediaType
import java.lang.reflect.Type

@ExperimentalSerializationApi
internal sealed class Serializer<R : Any> {

    abstract fun <T> fromEvent(loader: DeserializationStrategy<T>, body: R): T
    abstract fun <T> toEvent(contentType: MediaType, saver: SerializationStrategy<T>, value: T): R

    protected abstract val format: SerialFormat

    operator fun invoke(type: Type): KSerializer<Any> = format.serializersModule.serializer(type)

    class FromString(override val format: StringFormat) : Serializer<String>() {

        override fun <T> fromEvent(loader: DeserializationStrategy<T>, body: String): T {
            return format.decodeFromString(loader, body)
        }

        override fun <T> toEvent(contentType: MediaType, saver: SerializationStrategy<T>, value: T): String {
            return format.encodeToString(saver, value)
        }
    }

    class FromBytes(override val format: BinaryFormat) : Serializer<ByteArray>() {
        override fun <T> fromEvent(loader: DeserializationStrategy<T>, body: ByteArray): T {
            return format.decodeFromByteArray(loader, body)
        }

        override fun <T> toEvent(contentType: MediaType, saver: SerializationStrategy<T>, value: T): ByteArray {
            return format.encodeToByteArray(saver, value)
        }
    }
}
