package mr.adkhambek.moon.service

import mr.adkhambek.moon.EventFactory
import mr.adkhambek.moon.ServiceMethod
import mr.adkhambek.moon.adapter.EventAdapter
import mr.adkhambek.moon.flowResponse

class FlowServiceMethod(
    private val requestFactory: EventFactory,
    private val adapterFactory: EventAdapter.Factory,
) : ServiceMethod<Any>(requestFactory) {

    override fun invoke(event: String, args: Array<Any>): Any {
        return flowResponse(event, requestFactory, adapterFactory)
    }
}