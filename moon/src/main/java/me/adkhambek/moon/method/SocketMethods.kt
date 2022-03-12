package me.adkhambek.moon.method

import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import me.adkhambek.moon.Logger
import me.adkhambek.moon.MethodConfigs
import me.adkhambek.moon.Moon
import me.adkhambek.moon.Utils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.Continuation


internal abstract class SocketMethods(
    moon: Moon, methodConfigs: MethodConfigs,
) : ServiceMethod<Any>(moon, methodConfigs) {

    override fun invoke(args: Array<Any>): Any {

        val event: String = methodConfigs.getSocketEvent()
        val returnType: Type = methodConfigs.returnType
        val socket = moon.socket
        val logger = moon.logger

        if (Utils.getRawType(returnType) == Flow::class.java) {
            val genericArrayType: Type = Utils.getParameterLowerBound(0, returnType as ParameterizedType)

            return listen(
                event = event,
                socket = socket,
                logger = logger,
                returnType = genericArrayType,
            )
        }

        methodConfigs
            .parameterTypes
            .lastOrNull()
            ?.let { lastItemType ->
                if (Utils.getRawType(lastItemType) == Continuation::class.java) {
                    val genericArrayType: Type = Utils.getParameterLowerBound(0, lastItemType as ParameterizedType)

                    return emit(
                        args = args,
                        event = event,
                        socket = socket,
                        logger = logger,
                        returnType = genericArrayType,
                    )
                }
            }

        throw RuntimeException("Can't support this method")
    }

    @Throws(Exception::class)
    public abstract fun emit(
        socket: Socket,
        event: String,
        args: Array<Any>,
        returnType: Type,
        logger: Logger,
    ): Any

    public abstract fun listen(
        socket: Socket,
        event: String,
        returnType: Type,
        logger: Logger,
    ): Any
}