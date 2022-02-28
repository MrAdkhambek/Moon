package me.adkhambek.moon.method

import me.adkhambek.moon.EventFactory
import me.adkhambek.moon.convertor.EventConvertor
import me.adkhambek.moon.flowResponse

internal class FlowServiceMethod(
    private val requestFactory: EventFactory,
    private val adapterFactory: EventConvertor.Factory,
) : ServiceMethod<Any>(requestFactory) {

    override fun invoke(event: String, args: Array<Any>): Any {
        return flowResponse(event, requestFactory, adapterFactory)
    }
}