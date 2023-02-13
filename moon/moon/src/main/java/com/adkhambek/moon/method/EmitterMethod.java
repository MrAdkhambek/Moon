package com.adkhambek.moon.method;

import com.adkhambek.moon.KotlinExtensions;
import com.adkhambek.moon.internal.R2;
import io.socket.client.Socket;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import com.adkhambek.moon.Logger;
import com.adkhambek.moon.Utils;
import com.adkhambek.moon.convertor.EventConvertor;
import com.adkhambek.moon.internal.MethodConfigs;
import com.adkhambek.moon.provider.BodyConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.adkhambek.moon.internal.ParameterHandler.ContinuationParameterHandler;
import static com.adkhambek.moon.internal.ParameterHandler.EventParameterHandler;

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

        var localArguments = args;
        final var paramTypes = getMethodConfigs().getParameterTypes();

        if (paramTypes.length > 0) {
            final var lastItemType = paramTypes[paramTypes.length - 1];
            final var eventParameter = EventParameterHandler.invoke(localArguments, getMethodConfigs());

            final var event = eventParameter.getSecond();
            localArguments = eventParameter.getFirst();

            if (Utils.getRawType(lastItemType) == Continuation.class) {
                final var genericArrayType = Utils.getParameterLowerBound(0, (ParameterizedType) lastItemType);
                final var continuationParameter = ContinuationParameterHandler.invoke(localArguments, getMethodConfigs());
                localArguments = continuationParameter.getFirst();

                return emit(
                        event,
                        localArguments,
                        genericArrayType,
                        eventParameter.getThird(),
                        continuationParameter.getSecond()
                );
            }
        }

        throw Utils.methodError(getMethodConfigs().getMethod(), "Can't support this method");
    }


    @NotNull
    private Object emit(
            @NotNull String event,
            @NotNull Object[] args,
            @NotNull Type returnType,
            @NotNull MethodConfigs methodConfigs,
            @NotNull Continuation<Object> continuation
    ) {
        final var eventArguments = new Object[args.length];

        // getParameterTypes va getParameterAnnotationsArray ni ham remove qilish kerak
        // huddi args dan o'chirilgani kabi
        for (int i = 0; i < eventArguments.length; i++) {
            final EventConvertor<Object, String> convertor = createRequestConverter(
                    methodConfigs.getParameterTypes()[i],
                    methodConfigs.getParameterAnnotationsArray()[i]
            );

            final var arg = convertor.invoke(args[i]);
            try {
                eventArguments[i] = new JSONObject(arg);
            } catch (JSONException e) {
                throw Utils.parameterError(methodConfigs.getMethod(), e, i, "this param is not json");
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