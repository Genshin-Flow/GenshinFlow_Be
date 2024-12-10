package com.next.genshinflow.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class DesiredResponseLoader {

    private static final ObjectMapper objectMapper;
    private static final Map<String, Object> desiredResponseMap = new HashMap<>();

    static {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        objectMapper.registerModule(new JavaTimeModule());
    }

    private DesiredResponseLoader() {
    }

    public static String getByName(String name) throws JsonProcessingException {
        return objectMapper.writeValueAsString(MapUtils.getObject(desiredResponseMap, name));
    }

    public static void load() throws IOException {
        if (desiredResponseMap.isEmpty()) {
            loadResponse();
        }
    }

    private static void loadResponse() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("responses/**/*.json");
        for (Resource resource : resources) {
            desiredResponseMap.putAll(
                objectMapper.readValue(resource.getFile(), new TypeReference<>() {
                }));
        }
    }
}
