@file:JvmSynthetic
package me.adkhambek.moon.internal

import me.adkhambek.moon.convertor.EventConvertor
import me.adkhambek.moon.provider.BodyConverterProvider
import java.lang.reflect.Type

internal class LocalBodyConverterProvider constructor(
    private val converterFactories: List<EventConvertor.Factory>,
) : BodyConverterProvider {

    override fun <T> responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T> {
        return nextResponseBodyConverter<T>(null, type, annotations)
    }

    override fun <T> nextResponseBodyConverter(
        skipPast: EventConvertor.Factory?,
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T> {

        val start: Int = converterFactories.indexOf(skipPast) + 1
        val end = converterFactories.size

        for (index in start until end) {
            val factory: EventConvertor.Factory = converterFactories[index]

            val convertor = factory.fromEvent(
                type = type,
                annotations = annotations,
                converterProvider = this
            ) ?: continue

            return convertor as EventConvertor<String, T>
        }

        val builder = StringBuilder("Could not locate Result Body converter for ").append(type).append(".\n")

        if (skipPast != null) {
            builder.append("  Skipped:")
            for (index in 0 until start) {
                builder.append("\n   * ").append(converterFactories[index].javaClass.name)
            }
            builder.append('\n')
        }

        builder.append("  Tried:")
        for (index in start until end) {
            builder.append("\n   * ").append(converterFactories[index].javaClass.name)
        }

        throw IllegalArgumentException(builder.toString())
    }

    /////////////////////////////////////////////
    // Request
    /////////////////////////////////////////////

    override fun <T> requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>
    ): EventConvertor<T, String> {
        return nextRequestBodyConverter<T>(null, type, parameterAnnotations, methodAnnotations)
    }

    override fun <T> nextRequestBodyConverter(
        skipPast: EventConvertor.Factory?,
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
    ): EventConvertor<T, String> {

        val start: Int = converterFactories.indexOf(skipPast) + 1
        val end = converterFactories.size

        for (index in start until end) {
            val factory: EventConvertor.Factory = converterFactories[index]

            val convertor = factory.toEvent(
                type = type,
                parameterAnnotations = parameterAnnotations,
                methodAnnotations = methodAnnotations,
                converterProvider = this
            ) ?: continue

            return convertor as EventConvertor<T, String>
        }

        val builder = StringBuilder("Could not locate Event Body converter for ").append(type).append(".\n")

        if (skipPast != null) {
            builder.append("  Skipped:")
            for (index in 0 until start) {
                builder.append("\n   * ").append(converterFactories[index].javaClass.name)
            }
            builder.append('\n')
        }

        builder.append("  Tried:")
        for (index in start until end) {
            builder.append("\n   * ").append(converterFactories[index].javaClass.name)
        }

        throw IllegalArgumentException(builder.toString())
    }
}