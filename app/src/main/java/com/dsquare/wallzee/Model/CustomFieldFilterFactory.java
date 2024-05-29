package com.dsquare.wallzee.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CustomFieldFilterFactory implements TypeAdapterFactory {


    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                JsonElement tree = delegate.toJsonTree(value);
                tree.getAsJsonObject().remove("get_ads");
                if (tree.isJsonObject()) {
                    tree = filterField((JsonObject) tree);
                }
                elementAdapter.write(out, tree);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement tree = elementAdapter.read(in);
                if (tree.isJsonObject()) {
                    tree = filterField((JsonObject) tree);
                }
                Log.d("TAG", "read: "+delegate.toJson((T) tree).contains("get_ads"));
                return delegate.fromJsonTree(tree);
            }

            private JsonObject filterField(JsonObject object) {
                if (object.has("get_ads")) {
                    object.remove("get_ads");
                }
                return object;
            }
        }.nullSafe();
    }
}