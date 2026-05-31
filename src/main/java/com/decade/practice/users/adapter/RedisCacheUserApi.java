package com.decade.practice.users.adapter;

import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@Primary
@Component
@ConditionalOnProperty(name = "redis.cache.enabled", havingValue = "true")
public class RedisCacheUserApi implements UserApi {

    private static final String USER_INFO_KEY_PREFIX = "user_info:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final UserApi users;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheUserApi(@Qualifier("persistentUserApi") UserApi users, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.users = users;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private static String makeUserKey(UUID userId) {
        return USER_INFO_KEY_PREFIX + userId.toString();
    }

    @Override
    @Observed(name = "users.infos", lowCardinalityKeyValues = {
        "hit", "caching"
    })
    public Map<UUID, UserInfo> getUserInfo(Set<UUID> ids) {
        List<UUID> idList = ids.stream().toList();
        List<String> idStrings = idList.stream().map(RedisCacheUserApi::makeUserKey).toList();
        List<String> userStrings = redisTemplate.opsForValue().multiGet(idStrings);
        Set<UUID> missingIds = new HashSet<>();
        Set<String> invalidKeys = new HashSet<>();
        Map<UUID, UserInfo> result = new HashMap<>();

        for (int index = 0; index < idList.size(); index++) {
            String userString = userStrings.get(index);
            if (userString != null) {
                try {
                    UserInfo userInfo = objectMapper.readValue(userString, UserInfo.class);
                    result.put(userInfo.id(), userInfo);
                    continue;
                } catch (Exception e) {
                    log.warn("Failed to deserialize cached user info, treating as cache miss", e);
                    invalidKeys.add(makeUserKey(idList.get(index)));
                }
            }
            missingIds.add(idList.get(index));
        }

        if (!missingIds.isEmpty()) {
            Map<UUID, UserInfo> missingUsers = users.getUserInfo(missingIds);
            result.putAll(missingUsers);

            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Nullable
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    missingUsers.forEach((id, userInfo) -> {
                        try {
                            String userString = objectMapper.writeValueAsString(userInfo);
                            connection.stringCommands().setEx(makeUserKey(id).getBytes(), TTL.toSeconds(), userString.getBytes());
                        } catch (Exception e) {
                            log.warn("Failed to cache user info for " + id, e);
                        }
                    });
                    return null;
                }
            });
        }
        return result;
    }

}
