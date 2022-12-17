package me.adkhambek.moon.method;

import io.socket.client.Socket;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import me.adkhambek.moon.KotlinExtensions;
import me.adkhambek.moon.Logger;
import me.adkhambek.moon.Utils;
import me.adkhambek.moon.convertor.EventConvertor;
import me.adkhambek.moon.internal.MethodConfigs;
import me.adkhambek.moon.internal.R2;
import me.adkhambek.moon.provider.BodyConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class EmitterMethod extends ServiceMethod<Object> {

    @NotNull
    private final Socket socket;
    @NotNull
    private final Logger logger;

    public EmitterMethod(
            @NotNull BodyConverterProvider converterProvider,
            @NotNull MethodConfigs methodConfigs,
            @NotNull Socket socket,
            @NotNull Logger logger
    ) {
        super(converterProvider, methodConfigs);
        this.socket = socket;
        this.logger = logger;
    }

    @Override
    public Object invoke(@NotNull Object[] args) {
        final var event = getMethodConfigs().getSocketEvent();
        final var paramTypes = getMethodConfigs().getParameterTypes();

        if (paramTypes.length > 0) {
            final var lastItemType = paramTypes[paramTypes.length - 1];

            if (Utils.getRawType(lastItemType) == Continuation.class) {
                final var genericArrayType = Utils.getParameterLowerBound(0, (ParameterizedType) lastItemType);

                return emit(
                        event,
                        args,
                        genericArrayType
                );
            }
        }

        throw Utils.methodError(getMethodConfigs().getMethod(), "Can't support this method");
    }


    @NotNull
    private Object emit(
            @NotNull String event,
            @NotNull Object[] args,
            @NotNull Type returnType
    ) {
        final var continuation = (Continuation<Object>) args[args.length - 1];
        final var eventArguments = new Object[args.length - 1];

        for (int i = 0; i < eventArguments.length; i++) {
            final EventConvertor<Object, String> convertor = createRequestConverter(
                    getMethodConfigs().getParameterTypes()[i],
                    getMethodConfigs().getParameterAnnotationsArray()[i]
            );

            final var arg = convertor.invoke(args[i]);
            try {
                eventArguments[i] = new JSONObject(arg);
            } catch (JSONException e) {
                throw Utils.parameterError(getMethodConfigs().getMethod(), e, i, "this param is not json");
            } finally {
                logger.log(event, String.format(R2.REQUEST_WITH_RESULT, i, arg));
            }
        }

        if (returnType == Unit.class) return emit(
                event,
                eventArguments,
                continuation
        );
        else return emitWithAck(
                event,
                eventArguments,
                returnType,
                continuation
        );
    }

    @NotNull
    private Object emit(
            String event,
            Object[] args,
            Continuation<Object> continuation
    ) {
        try {
            return KotlinExtensions.emitWithoutResponse(
                    socket,
                    event,
                    logger,
                    args,
                    continuation
            );
        } catch (Exception e) {
            return KotlinExtensions.suspendAndThrow(e, continuation);
        }
    }

    @NotNull
    private Object emitWithAck(
            String event,
            Object[] args,
            Type returnType,
            Continuation<Object> continuation
    ) {
        try {
            EventConvertor<String, Object> convertor = createResponseConverter(returnType);

            return KotlinExtensions.emitWithResponse(
                    socket,
                    event,
                    logger,
                    convertor,
                    args,
                    continuation
            );
        } catch (Exception e) {
            return KotlinExtensions.suspendAndThrow(e, continuation);
        }
    }
}