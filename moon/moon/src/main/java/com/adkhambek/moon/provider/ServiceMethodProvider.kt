@file:JvmSynthetic

package com.adkhambek.moon.provider

import com.adkhambek.moon.method.ServiceMethod
import java.lang.reflect.Method

internal interface ServiceMethodProvider {
    operator fun <T> invoke(method: Method): ServiceMethod<T>
}

