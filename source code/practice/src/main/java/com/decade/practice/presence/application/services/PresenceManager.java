package com.decade.practice.presence.application.services;

import com.decade.practice.presence.application.ports.in.PresenceSetter;
import com.decade.practice.presence.application.ports.out.PresenceRepository;
import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.domain.Presence;
import com.decade.practice.presence.dto.PresenceResponse;
import com.decade.practice.presence.dto.mapper.PresenceMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PresenceManager implements PresenceService, PresenceSetter {

    private static final String KEYSPACE = "PRESENCES";
    private static final int TTL = 5 * 60;

    private final PresenceRepository presences;
    private final ZSetOperations<String, Object> zSet;
    private final PresenceMapper presenceMapper;

    public PresenceManager(PresenceRepository presences, RedisTemplate<String, Object> redisTemplate, PresenceMapper presenceMapper) {
        this.presences = presences;
        this.zSet = redisTemplate.opsForZSet();
        this.presenceMapper = presenceMapper;
    }

    private void evict() {
        zSet.removeRangeByScore(
                KEYSPACE, 0.0, Instant.now().getEpochSecond() - TTL
        );
    }

    @Override
    public PresenceResponse get(String username) {
        return presences.findById(username).map(presenceMapper::toResponse).orElseThrow();
    }

    @Override
    public List<PresenceResponse> getOnlineList(String username) {
        evict();
        Set<ZSetOperations.TypedTuple<Object>> rangeWithScores = zSet.rangeWithScores(KEYSPACE, 0, -1);

        if (rangeWithScores == null) {
            return new ArrayList<>();
        }

        List<Presence> result = rangeWithScores.stream()
                .map(tuple -> {
                    String who = (String) tuple.getValue();
                    return presences.findById(who).orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        return result.stream()
                .filter(status -> !status.getUsername().equals(username))
                .map(presenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PresenceResponse set(UUID userId, String username, String name, String avatar, Instant at) {
        evict();
        Presence presence = new Presence(username, userId, at, avatar, name);
        presences.save(presence);
        zSet.add(KEYSPACE, presence.getUsername(), presence.getAt().getEpochSecond());
        return presenceMapper.toResponse(presence);
    }
}