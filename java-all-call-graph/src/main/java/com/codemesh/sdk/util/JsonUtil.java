package com.codemesh.sdk.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fio
 * @date 2025/3/5
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJsonString(Object o) {
        if (o == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.error("Exception when parsing object to string. The object is: {}", o);
            log.error("Exception is", e);
            return o.getClass().isArray() ? "[]" : "{}";
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Exception when paring json to object. The json is : {}, clazz is {}", json, clazz);
            log.error("Exception is", e);
            return null;
        }
    }

}
