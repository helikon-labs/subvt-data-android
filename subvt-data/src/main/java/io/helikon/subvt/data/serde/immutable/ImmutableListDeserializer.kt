package io.helikon.subvt.data.serde.immutable

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ImmutableListDeserializer : JsonDeserializer<ImmutableList<*>> {
    override fun deserialize(
        json: JsonElement?,
        type: Type?,
        context: JsonDeserializationContext?,
    ): ImmutableList<*> {
        val typeArguments = (type as ParameterizedType).actualTypeArguments
        val parameterizedType =
            Types.collectionOf<Any>(
                typeArguments[0],
            ).type
        val collection = context!!.deserialize<Collection<*>>(json, parameterizedType)
        return collection.toImmutableList()
    }
}
