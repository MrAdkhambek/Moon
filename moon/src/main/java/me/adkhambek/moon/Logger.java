package me.adkhambek.moon;


public interface Logger {
    static final String INFO_FORMAT = "%s - %s";

    default void log(String event, String message) {
        apply(String.format(INFO_FORMAT, event, message));
    }

    default void e(String event, Throwable t) {
        apply(String.format(INFO_FORMAT, event, KotlinExtensions.getStackTraceString(t)));
    }

    void apply(String message);
}
