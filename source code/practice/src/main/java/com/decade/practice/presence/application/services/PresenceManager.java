package com.decade.practice.presence.application.services;

import com.decade.practice.presence.application.ports.in.PresenceSetter;
import com.decade.practice.presence.application.ports.out.PresenceRepository;
import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.domain.Presence;
import com.decade.practice.presence.dto.ChatPresenceResponse;
import com.decade.practice.presence.dto.PresenceRecommendationResponse;
import com.decade.practice.presence.dto.mapper.PresenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.decade.practice.presence.utils.EnthusiastUtils.determineEnthusiastId;

@Service
@RequiredArgsConstructor
public class PresenceManager implements PresenceService, PresenceSetter {

      private static final String KEYSPACE = "PRESENCES";
      private static final int TTL = 5 * 60;

      private final PresenceRepository presences;
      private final RedisTemplate<String, Object> redisTemplate;
      private final PresenceMapper presenceMapper;


      @Override
      public Map<String, ChatPresenceResponse> find(UUID caller, Set<String> chatIds) {
            List<String> chatList = chatIds.stream().toList();
            List<Object> rawResults = redisTemplate.executePipelined(
                      (RedisCallback<Object>) connection -> {

                            chatList.forEach(chatId -> {
                                  connection.zSetCommands().zRevRange(determineEnthusiastId(chatId).getBytes(), 0, 9);
                            });
                            return null;
                      }
            );

            Map<String, ChatPresenceResponse> result = new HashMap<>();
            Map<UUID, List<String>> enthusiastMap = new HashMap<>();

            Set<UUID> allEnthusiasts = new HashSet<>();

            for (int i = 0; i < rawResults.size(); i++) {
                  Set<Object> rawSet = (Set<Object>) rawResults.get(i);

                  String chatId = chatList.get(i);

                  List<UUID> enthusiasts = rawSet.stream()
                            .map(obj -> {
                                  if (obj instanceof byte[] bytes) {
                                        return new String(bytes, StandardCharsets.UTF_8);
                                  }
                                  return (String) obj;
                            })
                            .map(UUID::fromString)
                            .toList();

                  enthusiasts.forEach(
                            enthusiast -> {
                                  enthusiastMap.compute(enthusiast, new BiFunction<>() {
                                        @Override
                                        public List<String> apply(UUID uuid, List<String> chatIds) {
                                              if (chatIds == null)
                                                    chatIds = new ArrayList<>();

                                              chatIds.add(chatId);
                                              return chatIds;
                                        }
                                  });
                            }
                  );
                  allEnthusiasts.addAll(enthusiasts);
            }

            allEnthusiasts.remove(caller);

            Iterable<Presence> presences = this.presences.findAllById(allEnthusiasts);

            presences.forEach(presence -> {
                  List<String> correspondingChatIds = enthusiastMap.get(presence.getUserId());
                  correspondingChatIds.forEach(correspondingChatId -> {

                        result.compute(correspondingChatId, new BiFunction<>() {
                              @Override
                              public ChatPresenceResponse apply(String chatId, ChatPresenceResponse chatPresenceResponse) {
                                    Instant at = presence.getAt();
                                    if (chatPresenceResponse != null) {
                                          at = at.isAfter(chatPresenceResponse.at())
                                                    ? at : chatPresenceResponse.at();
                                    }
                                    return new ChatPresenceResponse(chatId, at);
                              }
                        });
                  });
            });

            return result;
      }


      private void evict() {
            redisTemplate.opsForZSet().removeRangeByScore(
                      KEYSPACE, 0.0, Instant.now().getEpochSecond() - TTL
            );
      }

      @Override
      public List<PresenceRecommendationResponse> findRecommendation(UUID userId) {
            evict();
            Set<ZSetOperations.TypedTuple<Object>> rangeWithScores = redisTemplate.opsForZSet().rangeWithScores(KEYSPACE, 0, -1);

            if (rangeWithScores == null) {
                  return new ArrayList<>();
            }

            List<Presence> result = rangeWithScores.stream()
                      .map(tuple -> {
                            String who = (String) tuple.getValue();
                            return presences.findById(UUID.fromString(who)).orElse(null);
                      })
                      .filter(Objects::nonNull)
                      .toList();

            return result.stream()
                      .filter(status -> !status.getUserId().equals(userId))
                      .map(presenceMapper::map)
                      .collect(Collectors.toList());
      }

      @Override
      public PresenceRecommendationResponse set(UUID userId, String name, String avatar, Instant at) {
            evict();
            Presence presence = new Presence(userId, at, avatar, name);
            redisTemplate.opsForZSet().add(KEYSPACE, presence.getUserId().toString(), presence.getAt().getEpochSecond());
            presences.save(presence);
            return presenceMapper.map(presence);
      }
}