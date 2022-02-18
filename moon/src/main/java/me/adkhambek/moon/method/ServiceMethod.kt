package me.adkhambek.moon.method

import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import me.adkhambek.moon.EventFactory
import me.adkhambek.moon.Utils
import me.adkhambek.moon.convertor.EventConvertor
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.Continuation


abstract class ServiceMethod<T>(
    private val eventFactory: EventFactory
) {

    @Throws(Exception::class)
    protected abstract operator fun invoke(event: String, args: Array<Any>): T

    public fun invoke(args: Array<Any>): T {
        val socketEvent = EventFactory.getSocketEvent(method = eventFactory.method)
        return invoke(socketEvent, args)
    }

    companion object {

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        public fun <T> parseAnnotations(
            socket: Socket,
            method: Method,
            adapterFactory: EventConvertor.Factory,
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

            TODO("Can't support this method")
        }
    }
}