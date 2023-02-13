package com.adkhambek.moon

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Event(val value: String)
