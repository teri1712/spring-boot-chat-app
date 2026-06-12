package com.decade.practice.presence.adapter;

import com.decade.practice.presence.application.ports.out.ScoreEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FreqRecScoreEngine implements ScoreEngine {

    private static final String KEY_PREFIX = "ranking-scores:";

    @Value("${scoring.recency.decay}")
    double decay = 1e-5;
    @Value("${scoring.frequency.bonus}")
    double bonus = 10;
    @Value("${scoring.capacity}")
    long capacity = 500;

    final RedisTemplate<String, Object> redisTemplate;

    private double computeRecency(double now) {
        return now * decay;
    }


    @Override
    public void incScore(String collection, String entity) {

        String collectionKey = determineKey(collection);
        String timestampKey = determineTimestampKey(collection, entity);

        Object timestamp = redisTemplate.opsForValue().get(timestampKey);
        double old = timestamp == null ? Instant.now().getEpochSecond() : (Integer) timestamp;
        double now = Instant.now().getEpochSecond();

        redisTemplate.execute(new SessionCallback<Object>() {
            @Nullable
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {

                RedisOperations<String, Object> ops = (RedisOperations<String, Object>) operations;

                ops.opsForZSet().addIfAbsent(collectionKey, entity, computeRecency(now));
                ops.opsForZSet().incrementScore(collectionKey, entity, bonus + computeRecency(now) - computeRecency(old));
                ops.opsForZSet().removeRange(collectionKey, 0, -capacity);
                ops.opsForValue().set(timestampKey, now);

                return null;
            }
        });

    }

    public static String determineKey(String collection) {
        return KEY_PREFIX + collection;
    }

    public static String determineTimestampKey(String collection, String entity) {
        return KEY_PREFIX + collection + "->" + entity;
    }


    @Override
    public double getScore(String collection, String value) {
        return Optional.ofNullable(redisTemplate.opsForZSet()
                .score(determineKey(collection), value))
            .orElse(0.0);
    }

    @Override
    public List<String> findTopK(String collection, int limit) {
        Set<Object> top = redisTemplate.opsForZSet().reverseRange(determineKey(collection), 0, limit - 1);
        return top == null ? List.of() :
            top.stream().map(Object::toString)
                .toList();
    }

    @Override
    public Map<String, List<String>> findTopK(Set<String> collections, int limit) {

        List<String> collectionList = collections.stream().toList();
        List<Object> pipes = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                collectionList.forEach(collection -> {
                    connection.zSetCommands().zRevRange(determineKey(collection).getBytes(), 0, limit - 1);
                });
                return null;
            }
        });
        Map<String, List<String>> result = new HashMap<>();
        for (int i = 0; i < collectionList.size(); i++) {
            String collection = collectionList.get(i);
            Set<Object> rawSet = (Set<Object>) pipes.get(i);

            List<String> values = rawSet.stream()
                .map(obj -> {
                    if (obj instanceof byte[] bytes) {
                        return new String(bytes, StandardCharsets.UTF_8);
                    }
                    return (String) obj;
                })
                .toList();
            result.put(collection, values);
        }
        return result;
    }
}
