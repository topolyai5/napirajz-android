package hu.napirajz.android;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

import hu.napirajz.android.response.NapirajzData;
import hu.napirajz.android.response.NapirajzResponse;

public class NapirajzDeserializer implements JsonDeserializer<NapirajzResponse> {
    @Override
    public NapirajzResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        NapirajzData data = context.deserialize(json.getAsJsonObject().entrySet().iterator().next().getValue(), NapirajzData.class);
        return new NapirajzResponse(data);
    }
}
