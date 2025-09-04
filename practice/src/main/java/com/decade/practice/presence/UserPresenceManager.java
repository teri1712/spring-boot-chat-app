package com.decade.practice.presence;

import com.decade.practice.models.OnlineStatus;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.usecases.ConversationRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserPresenceManager implements UserPresenceService {
        private static final String KEYSPACE = "ONLINE_USERS";
        private static final int TTL = 5 * 60;

        private final OnlineRepository onlineRepo;
        private final ConversationRepository conversationRepository;
        private final ZSetOperations<Object, Object> zSet;

        public UserPresenceManager(
                OnlineRepository onlineRepo,
                ConversationRepository conversationRepository,
                RedisTemplate<Object, Object> redisTemplate
        ) {
                this.onlineRepo = onlineRepo;
                this.conversationRepository = conversationRepository;
                this.zSet = redisTemplate.opsForZSet();
        }

        /**
         * Removes expired online statuses.
         */
        private void evict() {
                zSet.removeRangeByScore(
                        KEYSPACE, 0.0, Instant.now().getEpochSecond() - TTL
                );
        }

        @Override
        public OnlineStatus set(User user, long at) {
                return set(user.getUsername(), at);
        }

        @Override
        public OnlineStatus set(String username, long at) {
                evict();
                OnlineStatus status = onlineRepo.save(new OnlineStatus(username, at));
                zSet.add(KEYSPACE, status.getUsername(), (double) status.getAt());
                return status;
        }


        @Override
        public OnlineStatus get(String username) {
                OnlineStatus status = onlineRepo.findById(username).orElse(new OnlineStatus(username, 0));
                status.setUser(conversationRepository.getUser(username));
                return status;
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
                                long at = tuple.getScore().longValue();
                                OnlineStatus status = new OnlineStatus(who, at);
                                status.setUser(conversationRepository.getUser(who));
                                return status;
                        })
                        .collect(Collectors.toList());

                return result.stream()
                        .filter(status -> !status.getUsername().equals(username))
                        .collect(Collectors.toList());
        }
}