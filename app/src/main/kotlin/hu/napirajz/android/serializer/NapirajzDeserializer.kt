package hu.napirajz.android.serializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import hu.napirajz.android.response.NapirajzData
import hu.napirajz.android.response.NapirajzResponse
import java.lang.reflect.Type
import java.util.*

class NapirajzDeserializer : JsonDeserializer<NapirajzResponse> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NapirajzResponse {
        val ret = ArrayList<NapirajzData>()
        if (json.isJsonObject) {
            val entrySet = json.asJsonObject.entrySet()
            entrySet.forEach {
                val data = context.deserialize<NapirajzData>(it.value, NapirajzData::class.java)
                ret.add(data)
            }
        }
        return NapirajzResponse(ret)
    }
}
