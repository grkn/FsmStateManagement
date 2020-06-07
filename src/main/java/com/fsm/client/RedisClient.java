package com.fsm.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    public RedisClient(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void setJsonValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> T getJsonValue(String key, Class<T> clazz) {
        try {
            return objectMapper.readValue(Objects.requireNonNull(redisTemplate.opsForValue().get(key)), clazz);
        } catch (Exception e) {
            logger.error("Json Processing Exception with given key : {} from redis", key);
        }
        return null;
    }

    public <T> T getJsonValueAsCollection(String key, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(Objects.requireNonNull(redisTemplate.opsForValue().get(key)), typeReference);
        } catch (Exception e) {
            logger.error("Json Processing Exception with given key : {} from redis", key);
        }
        return null;
    }

    public Boolean clearJsonValue(String key) {
        return redisTemplate.delete(key);
    }
}
