package me.adkhambek.moon.method;

import io.socket.client.Socket;
import kotlinx.coroutines.flow.Flow;
import me.adkhambek.moon.KotlinExtensions;
import me.adkhambek.moon.Logger;
import me.adkhambek.moon.Utils;
import me.adkhambek.moon.convertor.EventConvertor;
import me.adkhambek.moon.internal.MethodConfigs;
import me.adkhambek.moon.provider.BodyConverterProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ListenerMethod extends ServiceMethod<Object> {

    @NotNull private final Socket socket;
    @NotNull private final Logger logger;

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
        String event = getMethodConfigs().getSocketEvent();
        Type returnType = getMethodConfigs().getReturnType();

        if (Utils.getRawType(returnType).equals(Flow.class)) {
            Type genericArrayType = Utils.getParameterLowerBound(0, (ParameterizedType) returnType);

            return listen(
                    event,
                    genericArrayType
            );
        }

        throw Utils.methodError(getMethodConfigs().getMethod(), "Can't support this method 2");
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
