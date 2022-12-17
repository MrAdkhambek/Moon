@file:JvmSynthetic

package me.adkhambek.moon.provider

import me.adkhambek.moon.method.ServiceMethod
import java.lang.reflect.Method

internal interface ServiceMethodProvider {
    operator fun <T> invoke(method: Method): ServiceMethod<T>
}

