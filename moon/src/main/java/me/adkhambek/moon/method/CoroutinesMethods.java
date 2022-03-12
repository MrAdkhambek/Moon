package me.adkhambek.moon.method;

import io.socket.client.Socket;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import me.adkhambek.moon.KotlinExtensions;
import me.adkhambek.moon.Logger;
import me.adkhambek.moon.MethodConfigs;
import me.adkhambek.moon.Moon;
import me.adkhambek.moon.convertor.EventConvertor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class CoroutinesMethods extends SocketMethods {

    public CoroutinesMethods(@NotNull MethodConfigs methodConfigs, @NotNull Moon moon) {
        super(moon, methodConfigs);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Object emit(
            @NotNull Socket socket,
            @NotNull String event,
            @NotNull Object[] args,
            @NotNull Type returnType,
            @NotNull Logger logger
    ) throws JSONException {
        final Continuation<Object> continuation = (Continuation<Object>) args[args.length - 1];

        Object[] eventArguments = new Object[args.length - 1];
        for (int i = 0; i < eventArguments.length; i++) {
            final EventConvertor<Object, String> convertor = createRequestConverter(
                    getMethodConfigs().getParameterTypes()[i],
                    getMethodConfigs().getParameterAnnotationsArray()[i]
            );

            final String arg = convertor.invoke(args[i]);
            eventArguments[i] = new JSONObject(arg);
            logger.log(event, String.format("REQUEST - arg[%d] = %s", i, arg));
        }

        if (returnType == Unit.class)
            return emitWithoutResponse(
                    socket,
                    event,
                    eventArguments,
                    logger,
                    continuation
            );
        else
            return emitWithResponse(socket,
                    event,
                    eventArguments,
                    logger,
                    returnType,
                    continuation
            );
    }

    @NotNull
    @Override
    public Object listen(
            @NotNull Socket socket,
            @NotNull String event,
            @NotNull Type returnType,
            @NotNull Logger logger
    ) {
        EventConvertor<String, Object> convertor = createResponseConverter(returnType);

        return KotlinExtensions.flowResponse(
                socket,
                event,
                logger,
                convertor
        );
    }

    private Object emitWithoutResponse(
            Socket socket,
            String event,
            Object[] args,
            Logger logger,
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

    private Object emitWithResponse(
            Socket socket,
            String event,
            Object[] args,
            Logger logger,
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
