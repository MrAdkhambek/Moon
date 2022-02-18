package me.adkhambek.moon.method;

import kotlin.coroutines.Continuation;
import me.adkhambek.moon.KotlinExtensions;
import me.adkhambek.moon.convertor.EventConvertor;
import me.adkhambek.moon.EventFactory;
import me.adkhambek.moon.convertor.EventConvertor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public final class SuspendServiceMethod extends ServiceMethod<Object> {

    private final EventFactory requestFactory;
    private final EventConvertor.Factory adapterFactory;

    public SuspendServiceMethod(EventFactory requestFactory, EventConvertor.Factory adapterFactory) {
        super(requestFactory);
        this.requestFactory = requestFactory;
        this.adapterFactory = adapterFactory;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected Object invoke(@NotNull String event, @NotNull Object[] args) throws JSONException {
        Continuation<Object> continuation = (Continuation<Object>) args[args.length - 1];
        Object[] eventArguments = new Object[args.length - 1];

        for (int i = 0; i < args.length- 1; i++) {
            EventConvertor<Object, String> adapter = adapterFactory.toEvent(
                    requestFactory.getParameterTypes()[i],
                    requestFactory.getParameterAnnotationsArray()[i]
            );

            eventArguments[i] = new JSONObject(adapter.invoke(args[i]));
        }

        try {
            return KotlinExtensions.awaitResponse(event, requestFactory, adapterFactory, eventArguments, continuation);
        } catch (Exception e) {
            return KotlinExtensions.suspendAndThrow(e, continuation);
        }
    }
}
