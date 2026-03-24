package com.decade.practice.users.application.services;

import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
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
@Profile("redis-cache")
@Component
@RequiredArgsConstructor
public class CacheableUserApi implements UserApi {

      private static final String USER_INFO_KEY_PREFIX = "user_info:";
      private static final Duration TTL = Duration.ofMinutes(5);

      private final UserApiImpl userApi;
      private final StringRedisTemplate redisTemplate;
      private final ObjectMapper objectMapper;

      private static String makeUserKey(UUID userId) {
            return USER_INFO_KEY_PREFIX + userId.toString();
      }

      @Override
      public Map<UUID, UserInfo> getUserInfo(Set<UUID> ids) {

            List<UUID> idList = ids.stream().toList();
            List<String> idStrings = idList.stream().map(CacheableUserApi::makeUserKey).toList();
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
                  Map<UUID, UserInfo> missingUsers = userApi.getUserInfo(missingIds);
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
