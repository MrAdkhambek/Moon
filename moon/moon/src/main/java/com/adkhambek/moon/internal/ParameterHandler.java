package com.adkhambek.moon.internal;

import kotlin.Pair;
import kotlin.Triple;
import kotlin.coroutines.Continuation;
import com.adkhambek.moon.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ParameterHandler<T> {

    @NotNull
    T invoke(
            @NotNull Object[] args,
            @NotNull MethodConfigs methodConfigs
    );

    ParameterHandler<Pair<Object[], Continuation<Object>>> ContinuationParameterHandler = (args, methodConfigs) -> {
        final var continuation = (Continuation<Object>) args[args.length - 1];
        final Object[] copyArray = new Object[args.length - 1];
        System.arraycopy(args, 0, copyArray, 0, args.length - 1);
        return new Pair<>(copyArray, continuation);
    };

    ParameterHandler<Triple<Object[], String, MethodConfigs>> EventParameterHandler = (args, methodConfigs) -> {

        var countRemovedElement = 0;
        var localArguments = args;

        var socketEvent = methodConfigs.getSocketEvent();
        var parameterTypes = methodConfigs.getParameterTypes();
        var parameterAnnotationsArray = methodConfigs.getParameterAnnotationsArray();

        for (int i = 0; i < parameterAnnotationsArray.length; ++i) {
            final var parameterAnnotations = parameterAnnotationsArray[i];
            for (final Annotation annotation : parameterAnnotations) {
                if (annotation instanceof Event) {
                    Event event = (Event) annotation;

                    localArguments = removeElement(localArguments, i - countRemovedElement);
                    parameterTypes = removeElement(parameterTypes, i - countRemovedElement);
                    parameterAnnotationsArray = removeElement(parameterAnnotationsArray, i - countRemovedElement);

                    socketEvent = socketEvent.replace("{" + event.value() + "}", (String) args[i]);
                    ++countRemovedElement;
                    break;
                }
            }
        }

        final var newMethodConfigs = new MethodConfigs(
                methodConfigs.getMethod(),
                methodConfigs.getReturnType(),
                methodConfigs.getMethodAnnotations(),
                parameterTypes,
                parameterAnnotationsArray
        );

        return new Triple<>(localArguments, socketEvent, newMethodConfigs);
    };

    @NotNull
    private static Object[] removeElement(@NotNull Object[] origin, int index) {
        // create an array to hold elements after deletion
        Object[] copyArray = new Object[origin.length - 1];

        // copy elements from original array from beginning till index into copyArray
        System.arraycopy(origin, 0, copyArray, 0, index);

        // copy elements from original array from index+1 till end into copyArray
        System.arraycopy(origin, index + 1, copyArray, index, origin.length - index - 1);

        return copyArray;
    }

    @NotNull
    private static Type[] removeElement(@NotNull Type[] origin, int index) {
        // create an array to hold elements after deletion
        Type[] copyArray = new Type[origin.length - 1];

        // copy elements from original array from beginning till index into copyArray
        System.arraycopy(origin, 0, copyArray, 0, index);

        // copy elements from original array from index+1 till end into copyArray
        System.arraycopy(origin, index + 1, copyArray, index, origin.length - index - 1);

        return copyArray;
    }

    @NotNull
    private static Annotation[][] removeElement(@NotNull Annotation[][] origin, int index) {
        // create an array to hold elements after deletion
        Annotation[][] copyArray = new Annotation[origin.length - 1][];

        // copy elements from original array from beginning till index into copyArray
        System.arraycopy(origin, 0, copyArray, 0, index);

        // copy elements from original array from index+1 till end into copyArray
        System.arraycopy(origin, index + 1, copyArray, index, origin.length - index - 1);

        return copyArray;
    }
}
