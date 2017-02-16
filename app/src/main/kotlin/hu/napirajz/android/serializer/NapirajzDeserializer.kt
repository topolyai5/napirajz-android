package hu.napirajz.android.serializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type

import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse

class NapirajzDeserializer : JsonDeserializer<NapirajzResponse> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NapirajzResponse {
        val data = context.deserialize<NapirajzData>(json.asJsonObject.entrySet().iterator().next().value, NapirajzData::class.java)
        return NapirajzResponse(data)
    }
}
