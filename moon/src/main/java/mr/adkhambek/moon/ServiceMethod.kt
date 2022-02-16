package mr.adkhambek.moon

import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import mr.adkhambek.moon.adapter.EventAdapter
import mr.adkhambek.moon.service.FlowServiceMethod
import mr.adkhambek.moon.service.SuspendServiceMethod
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.Continuation


abstract class ServiceMethod<T>(
    private val eventFactory: EventFactory
) {

    protected abstract operator fun invoke(event: String, args: Array<Any>): T

    fun invoke(args: Array<Any>): T {
        val socketEvent = EventFactory.getSocketEvent(method = eventFactory.method)
        return invoke(socketEvent, args)
    }

    companion object {

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> parseAnnotations(
            socket: Socket,
            method: Method,
            adapterFactory: EventAdapter.Factory,
        ): ServiceMethod<T> {

            val returnType = method.genericReturnType

            if (Utils.hasUnresolvableType(returnType)) {
                throw Utils.methodError(
                    method, "Method return type must not include a type variable or wildcard: %s", returnType
                )
            }

            if (returnType === Void.TYPE) {
                throw Utils.methodError(method, "Service methods cannot return void.")
            }

            if (Utils.getRawType(returnType) == Flow::class.java) {
                val genericArrayType: Type = Utils.getParameterLowerBound(0, returnType as ParameterizedType)
                val evenFactory = EventFactory(socket, method, genericArrayType)
                return FlowServiceMethod(evenFactory, adapterFactory) as ServiceMethod<T>
            }

            val lastItemType = method.genericParameterTypes.last()
            if (Utils.getRawType(lastItemType) == Continuation::class.java) {
                val genericArrayType: Type = Utils.getParameterLowerBound(0, lastItemType as ParameterizedType)
                val evenFactory = EventFactory(socket, method, genericArrayType)
                return SuspendServiceMethod(evenFactory, adapterFactory) as ServiceMethod<T>
            }

            TODO()
        }
    }
}