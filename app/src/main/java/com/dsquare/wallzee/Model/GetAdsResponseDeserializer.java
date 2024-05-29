package com.dsquare.wallzee.Model;

import com.dsquare.wallzee.Callback.CallbackAds;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GetAdsResponseDeserializer  implements JsonDeserializer<CallbackAds> {
    @Override
    public CallbackAds deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return null;
    }
}
