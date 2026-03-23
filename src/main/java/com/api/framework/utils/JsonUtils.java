package com.api.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * JsonUtils - Utility class for JSON serialization/deserialization operations.
 * Wraps Jackson ObjectMapper with error handling and convenience methods.
 */
public class JsonUtils {

    private static final Logger log = LogManager.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {}

    /**
     * Serialize an object to JSON string
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage());
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Serialize an object to pretty-printed JSON string
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to pretty JSON: {}", e.getMessage());
            throw new RuntimeException("JSON pretty serialization failed", e);
        }
    }

    /**
     * Deserialize a JSON string to a specific class
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    /**
     * Deserialize a JSON string to a generic type (e.g., List<User>)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON with TypeReference: {}", e.getMessage());
            throw new RuntimeException("JSON deserialization with TypeRef failed", e);
        }
    }

    /**
     * Deserialize a RestAssured response body to a specific class
     */
    public static <T> T fromResponse(Response response, Class<T> clazz) {
        return fromJson(response.getBody().asString(), clazz);
    }

    /**
     * Convert a Response to a Map
     */
    public static Map<String, Object> responseToMap(Response response) {
        return fromJson(response.getBody().asString(),
                new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Convert any object to a Map (useful for payload building)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }

    /**
     * Load JSON from a file (for test data files in resources)
     */
    public static <T> T fromFile(String filePath, Class<T> clazz) {
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            log.error("Failed to read JSON from file '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("JSON file read failed", e);
        }
    }

    /**
     * Get the shared ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Check if a string is valid JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
