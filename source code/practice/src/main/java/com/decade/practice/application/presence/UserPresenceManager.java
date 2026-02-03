package com.decade.practice.application.presence;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.infra.security.jwt.JwtUser;
import com.decade.practice.persistence.redis.OnlineStatus;
import com.decade.practice.persistence.redis.repositories.OnlineRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserPresenceManager implements UserPresenceService {

    private static final String KEYSPACE = "ONLINE_USERS";
    private static final int TTL = 5 * 60;

    private final OnlineRepository onlineRepo;
    private final ZSetOperations<String, Object> zSet;

    public UserPresenceManager(OnlineRepository onlineRepo, RedisTemplate<String, Object> redisTemplate) {
        this.onlineRepo = onlineRepo;
        this.zSet = redisTemplate.opsForZSet();
    }

    private void evict() {
        zSet.removeRangeByScore(
                KEYSPACE, 0.0, Instant.now().getEpochSecond() - TTL
        );
    }

    @Override
    public OnlineStatus set(JwtUser user, Instant at) {
        evict();
        OnlineStatus status = new OnlineStatus();
        status.setUsername(user.getUsername());
        status.setAvatar(user.getClaims().getAvatar());
        status.setName(user.getClaims().getName());
        status.setUserId(user.getClaims().getId());
        status.setAt(at);

        onlineRepo.save(status);
        zSet.add(KEYSPACE, status.getUsername(), status.getAt().getEpochSecond());
        return status;
    }


    @Override
    public OnlineStatus get(String username) {
        return onlineRepo.findById(username).orElseThrow();
    }

    @Override
    public List<OnlineStatus> getOnlineList(String username) {
        evict();
        Set<ZSetOperations.TypedTuple<Object>> rangeWithScores = zSet.rangeWithScores(KEYSPACE, 0, -1);

        if (rangeWithScores == null) {
            return new ArrayList<>();
        }

        List<OnlineStatus> result = rangeWithScores.stream()
                .map(tuple -> {
                    String who = (String) tuple.getValue();
                    return onlineRepo.findById(who).orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        return result.stream()
                .filter(status -> !status.getUsername().equals(username))
                .collect(Collectors.toList());
    }
}