package com.decade.practice.presence.adapter;

import com.decade.practice.presence.application.ports.out.ScoreEngine;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class SimpleScoreEngine implements ScoreEngine {

      private static final String KEY_PREFIX = "ranking-scores:";


      private final RedisTemplate<String, Object> redisTemplate;

      private static double computeRecency(double now) {
            double decay = 1e-5;
            return now * decay;
      }

      private static double computeBonus(double old, double now) {
            double engagementScore = 10;
            return engagementScore + computeRecency(now) - computeRecency(old);
      }

      @Override
      public void incScore(String collection, String value) {

            Object timestamp = redisTemplate.opsForValue().get(determineTimestampKey(collection, value));

            double old = timestamp == null ? Instant.now().getEpochSecond() : (Integer) timestamp;
            double now = Instant.now().getEpochSecond();

            set(collection, value, now, computeBonus(old, now));

      }

      public static String determineKey(String collection) {
            return KEY_PREFIX + collection;
      }

      public static String determineTimestampKey(String collection, String value) {
            return KEY_PREFIX + collection + "->" + value;
      }

      private void set(String collection, String value, double timestamp, double bonus) {
            String key = determineKey(collection);
            long capacity = 500;
            redisTemplate.execute(new SessionCallback<Object>() {
                  @Nullable
                  @Override
                  public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {

                        RedisOperations<String, Object> ops = (RedisOperations<String, Object>) operations;

                        ops.opsForZSet().addIfAbsent(key, value, computeRecency(timestamp));
                        ops.opsForZSet().incrementScore(key, value, bonus);
                        ops.opsForZSet().removeRange(key, 0, -capacity);
                        ops.opsForValue().set(determineTimestampKey(collection, value), Instant.now().getEpochSecond());

                        return null;
                  }
            });

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
