package mr.adkhambek.moon

import io.socket.client.Socket
import mr.adkhambek.moon.adapter.EventAdapter
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap


class Moon private constructor(
    private val socket: Socket,
    private val adapterFactory: EventAdapter.Factory,
) {

    private val serviceMethodCache: MutableMap<Method, ServiceMethod<Any>> = ConcurrentHashMap()

    inline fun <reified T> create() = create(T::class.java)

    fun <T> create(clazz: Class<T>): T {
        val proxy = Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf<Class<*>>(clazz)
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
                val service = ServiceMethod.parseAnnotations<Any>(socket, method, adapterFactory)
                serviceMethodCache[method] = service
                result = service
            }
        }

        return requireNotNull(result)
    }

    class Builder {
        private var socket: Socket? = null
        private var adapterFactory: EventAdapter.Factory? = null

        fun with(socket: Socket): Builder {
            this.socket = socket
            return this
        }

        fun convertor(adapterFactory: EventAdapter.Factory): Builder {
            this.adapterFactory = adapterFactory
            return this
        }

        fun build(): Moon {
            val socket = requireNotNull(this.socket) { "Socket must not be null" }
            val adapterFactory = requireNotNull(this.adapterFactory) { "Adapter Factory must not be null" }

            return Moon(
                socket = socket,
                adapterFactory = adapterFactory
            )
        }
    }

    class Factory {
        fun create(socket: Socket, adapterFactory: EventAdapter.Factory): Moon = Moon(
            socket = socket,
            adapterFactory = adapterFactory
        )
    }
}