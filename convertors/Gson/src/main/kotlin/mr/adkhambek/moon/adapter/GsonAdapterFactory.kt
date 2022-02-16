package mr.adkhambek.moon.adapter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class GsonAdapterFactory constructor(
    private val gson: Gson
) : EventAdapter.Factory {

    constructor() : this(Gson())

    override fun fromEvent(type: Type, annotations: Array<Annotation>): EventAdapter<String, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return DeserializationStrategyConverter(adapter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun toEvent(type: Type, annotations: Array<Annotation>): EventAdapter<Any, String> {
        val adapter: TypeAdapter<out Any> = gson.getAdapter(TypeToken.get(type))
        return SerializationStrategyConverter(adapter as TypeAdapter<Any>)
    }
}