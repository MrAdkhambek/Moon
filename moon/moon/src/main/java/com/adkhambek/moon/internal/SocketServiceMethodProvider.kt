@file:[
    JvmName("SocketServiceMethodProvider")
    JvmSynthetic
]

package com.adkhambek.moon.internal

import com.adkhambek.moon.Logger
import com.adkhambek.moon.Utils
import com.adkhambek.moon.method.EmitterMethod
import com.adkhambek.moon.method.ListenerMethod
import com.adkhambek.moon.method.ServiceMethod
import com.adkhambek.moon.provider.BodyConverterProvider
import com.adkhambek.moon.provider.ServiceMethodProvider
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Method
import kotlin.coroutines.Continuation

internal class SocketServiceMethodProvider constructor(
    private val converterProvider: BodyConverterProvider,
    private val socket: Socket,
    private val logger: Logger,
) : ServiceMethodProvider {

    override fun <T> invoke(method: Method): ServiceMethod<T> {
        val returnType = method.genericReturnType
        val methodConfigs = MethodConfigs(method, returnType)

        if (Utils.hasUnresolvableType(returnType)) {
            throw Utils.methodError(
                method,
                "Method return type must not include a type variable or wildcard: %s",
                returnType
            )
        }

        if (returnType === Void.TYPE) {
            throw Utils.methodError(
                method,
                "Service methods cannot return void."
            )
        }

        if (Utils.getRawType(returnType) == Flow::class.java) {
            return ListenerMethod(
                converterProvider,
                methodConfigs,
                socket,
                logger,
            ) as ServiceMethod<T>
        }

        val lastItemType = method.genericParameterTypes.lastOrNull()
        if (lastItemType != null && Utils.getRawType(lastItemType) == Continuation::class.java) {
            return EmitterMethod(
                converterProvider,
                methodConfigs,
                socket,
                logger,
            ) as ServiceMethod<T>
        }

        throw Utils.methodError(method, "Can't support this method")
    }
}
