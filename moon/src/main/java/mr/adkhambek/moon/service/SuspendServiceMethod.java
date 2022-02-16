package mr.adkhambek.moon.service;

import kotlin.coroutines.Continuation;
import mr.adkhambek.moon.EventFactory;
import mr.adkhambek.moon.ServiceMethod;
import mr.adkhambek.moon.adapter.EventAdapter;
import mr.adkhambek.moon.KotlinExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class SuspendServiceMethod extends ServiceMethod<Object> {

    private final EventFactory requestFactory;
    private final EventAdapter.Factory adapterFactory;

    public SuspendServiceMethod(EventFactory requestFactory, EventAdapter.Factory adapterFactory) {
        super(requestFactory);
        this.requestFactory = requestFactory;
        this.adapterFactory = adapterFactory;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected Object invoke(@NotNull String event, @NotNull Object[] args) {
        Continuation<Object> continuation = (Continuation<Object>) args[args.length - 1];

        Object[] arguments = Arrays.copyOfRange(args, 0, args.length - 1);
        Object[] eventArguments = new Object[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            EventAdapter<Object, String> adapter = adapterFactory.toEvent(
                    requestFactory.getParameterTypes()[i],
                    requestFactory.getParameterAnnotationsArray()[i]
            );

            eventArguments[i] = adapter.convert(arguments[i]);
        }

        try {
            return KotlinExtensions.awaitResponse(event, requestFactory, adapterFactory, eventArguments, continuation);
        } catch (Exception e) {
            return KotlinExtensions.suspendAndThrow(e, continuation);
        }
    }
}
