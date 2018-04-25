package com.yibo.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(String string, Class<T> type) {
        try {
            return objectMapper.readValue(string, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json数组转List
     */
    public static <T> T toList(String string, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(string, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}