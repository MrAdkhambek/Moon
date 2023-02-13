package com.adkhambek.moon.method;

import com.adkhambek.moon.KotlinExtensions;
import io.socket.client.Socket;
import kotlinx.coroutines.flow.Flow;
import com.adkhambek.moon.Logger;
import com.adkhambek.moon.Utils;
import com.adkhambek.moon.convertor.EventConvertor;
import com.adkhambek.moon.internal.MethodConfigs;
import com.adkhambek.moon.provider.BodyConverterProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.adkhambek.moon.internal.ParameterHandler.EventParameterHandler;

public class ListenerMethod extends ServiceMethod<Object> {

    @NotNull
    private final Socket socket;
    @NotNull
    private final Logger logger;

    public ListenerMethod(
            @NotNull BodyConverterProvider converterProvider,
            @NotNull MethodConfigs methodConfigs,
            @NotNull Socket socket,
            @NotNull Logger logger
    ) {
        super(converterProvider, methodConfigs);
        this.socket = socket;
        this.logger = logger;
    }

    @NotNull
    @Override
    public Object invoke(@NotNull Object[] args) {
        Type returnType = getMethodConfigs().getReturnType();

        if (Utils.getRawType(returnType).equals(Flow.class)) {
            Type genericArrayType = Utils.getParameterLowerBound(0, (ParameterizedType) returnType);
            final var eventParameter = EventParameterHandler.invoke(args, getMethodConfigs());
            final var event = eventParameter.getSecond();

            return listen(
                    event,
                    genericArrayType
            );
        }

        throw Utils.methodError(getMethodConfigs().getMethod(), "Can't support this method");
    }

    @NotNull
    private Object listen(
            @NotNull String event,
            @NotNull Type returnType
    ) {
        EventConvertor<String, Object> convertor = createResponseConverter(returnType);

        return KotlinExtensions.flowResponse(
                socket,
                event,
                logger,
                convertor
        );
    }
}
