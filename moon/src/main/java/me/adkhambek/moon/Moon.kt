@file:Suppress("UNCHECKED_CAST")

package me.adkhambek.moon

import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.adkhambek.moon.convertor.EventConvertor
import me.adkhambek.moon.method.ServiceMethod
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

public class Moon private constructor(
    internal val socket: Socket,
    internal val logger: Logger,
    internal val converterFactories: List<EventConvertor.Factory>,
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
                val service = ServiceMethod.parseAnnotations<Any>(this, method)
                serviceMethodCache[method] = service
                result = service
            }
        }

        return requireNotNull(result)
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    //  Convertor methods
    // ///////////////////////////////////////////////////////////////////////////////////////////

    public fun <T> requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
    ): EventConvertor<T, String> {
        return nextRequestBodyConverter<T>(null, type, parameterAnnotations, methodAnnotations)
    }

    public fun <T> nextRequestBodyConverter(
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
                moon = this
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

    public fun <T> responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T> {
        return nextResponseBodyConverter<T>(null, type, annotations)
    }

    public fun <T> nextResponseBodyConverter(
        skipPast: EventConvertor.Factory?,
        type: Type,
        annotations: Array<Annotation>,
    ): EventConvertor<String, T> {

        val start: Int = converterFactories.indexOf(skipPast) + 1
        val end = converterFactories.size

        for (index in start until end) {
            val factory: EventConvertor.Factory = converterFactories[index]

            val convertor = factory.fromEvent(
                type = type, annotations = annotations, moon = this
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
    //  Moon creator classes
    // ///////////////////////////////////////////////////////////////////////////////////////////

    public class Builder {
        private var socket: Socket? = null
        private var logger: Logger? = null
        private var converterFactories: MutableList<EventConvertor.Factory> = arrayListOf()

        public fun with(socket: Socket): Builder {
            this.socket = socket
            return this
        }

        public fun logger(logger: Logger): Builder {
            this.logger = logger
            return this
        }

        public fun addConvertor(adapterFactory: EventConvertor.Factory): Builder {
            this.converterFactories.add(adapterFactory)
            return this
        }

        public fun build(): Moon {
            val socket = requireNotNull(this.socket) { "Socket must not be null" }
            val converterFactories = this.converterFactories
            val logger = this.logger ?: Logger { }

            return Moon(
                socket = socket, logger = logger, converterFactories = converterFactories
            )
        }
    }

    public class Factory {
        public fun create(
            socket: Socket,
            logger: Logger,
            vararg converterFactories: EventConvertor.Factory,
        ): Moon = Moon(
            socket = socket, logger = logger, converterFactories = converterFactories.toList()
        )
    }
}
