@file:[Suppress("UNCHECKED_CAST") JvmName("Moon") JvmSynthetic]

package com.adkhambek.moon

import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.adkhambek.moon.convertor.EventConvertor
import com.adkhambek.moon.method.ServiceMethod
import com.adkhambek.moon.internal.LocalBodyConverterProvider
import com.adkhambek.moon.internal.SocketServiceMethodProvider
import com.adkhambek.moon.provider.ServiceMethodProvider
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

public class Moon private constructor(
    private val socket: Socket,
    private val logger: Logger,
    private val serviceMethodProvider: ServiceMethodProvider
) {

    private val _state: MutableStateFlow<Status> = MutableStateFlow(Status.DISCONNECT)
    public val state: StateFlow<Status> get() = _state.asStateFlow()

    init {
        socket.on(Socket.EVENT_CONNECT) { args ->
            _state.update { Status.CONNECTED }
            logger.log(Socket.EVENT_CONNECT, args.joinToString(separator = " | ", transform = Any::toString))
        }

        socket.on(Socket.EVENT_DISCONNECT) { args ->
            _state.update { Status.DISCONNECT }
            logger.log(Socket.EVENT_DISCONNECT, args.joinToString(separator = " | ", transform = Any::toString))
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            _state.update { Status.EVENT_CONNECT_ERROR }
            logger.log(Socket.EVENT_CONNECT_ERROR, args.joinToString(separator = " | ", transform = Any::toString))
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    //  service creator methods
    // ///////////////////////////////////////////////////////////////////////////////////////////

    private val serviceMethodCache: MutableMap<Method, ServiceMethod<Any>> = ConcurrentHashMap()

    public inline fun <reified T> create(): T = create(T::class.java)

    public fun <T> create(clazz: Class<T>): T {
        val proxy = Proxy.newProxyInstance(
            clazz.classLoader, arrayOf<Class<*>>(clazz)
        ) { _: Any?, method: Method, nullableArgs: Array<Any>? ->
            val args: Array<Any> = nullableArgs ?: arrayOf()

            // If the method is a method from Object then defer to normal invocation.
            if (method.declaringClass == Any::class.java) {
                return@newProxyInstance method.invoke(this, *args)
            }

            loadServiceMethod(method).invoke(args)
        }

        return clazz.cast(proxy)
    }

    private fun loadServiceMethod(method: Method): ServiceMethod<*> {
        var result = serviceMethodCache[method]
        if (result != null) return result

        synchronized(serviceMethodCache) {
            result = serviceMethodCache[method]
            if (result == null) {
                val service = serviceMethodProvider<Any>(method)
                serviceMethodCache[method] = service
                result = service
            }
        }

        return requireNotNull(result)
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    //  socket connect/disconnect methods
    // ///////////////////////////////////////////////////////////////////////////////////////////

    public fun connect() {
        socket.connect()
    }

    public fun disconnect() {
        socket.disconnect()
    }

    public enum class Status {
        CONNECTED,
        DISCONNECT,
        EVENT_CONNECT_ERROR;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    //  Factory class
    // ///////////////////////////////////////////////////////////////////////////////////////////

    public class Factory {
        public fun create(
            socket: Socket,
            logger: Logger,
            vararg converterFactories: EventConvertor.Factory,
        ): Moon {

            val methodProvider: ServiceMethodProvider = SocketServiceMethodProvider(
                converterProvider = LocalBodyConverterProvider(converterFactories.toList()),
                socket = socket,
                logger = logger,
            )

            return Moon(
                socket = socket,
                logger = logger,
                serviceMethodProvider = methodProvider,
            )
        }
        // ///////////////////////////////////////////////////////////////////////////////////////////
        //  Companion object for extensions
        // ///////////////////////////////////////////////////////////////////////////////////////////
        public companion object
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    //  Companion object for extensions
    // ///////////////////////////////////////////////////////////////////////////////////////////
    public companion object
}
