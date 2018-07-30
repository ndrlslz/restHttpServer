package com.ndrlslz.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String encode(Object object) {
        return gson.toJson(object);
    }

    public static <T> T decode(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
