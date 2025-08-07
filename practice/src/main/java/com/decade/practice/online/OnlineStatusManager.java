package com.decade.practice.online;

import com.decade.practice.entities.OnlineStatus;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.usecases.core.OnlineStatistic;
import com.decade.practice.websocket.WsEntityRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OnlineStatusManager implements OnlineStatistic, ChannelInterceptor, HandshakeInterceptor {
      private static final String KEYSPACE = "ONLINE_USERS";
      private static final int TTL = 5 * 60;

      private final OnlineRepository onlineRepo;
      private final WsEntityRepository entityRepo;
      private final ZSetOperations<Object, Object> zSet;

      public OnlineStatusManager(
            OnlineRepository onlineRepo,
            WsEntityRepository entityRepo,
            RedisTemplate<Object, Object> redisTemplate
      ) {
            this.onlineRepo = onlineRepo;
            this.entityRepo = entityRepo;
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
            evict();
            OnlineStatus status = onlineRepo.save(new OnlineStatus(user, at));
            zSet.add(KEYSPACE, status.getUsername(), (double) status.getAt());
            return status;
      }

      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
            var principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());
            if (principal == null) {
                  return message;
            }
            set(entityRepo.getUser(principal.getName()));
            return message;
      }

      @Override
      public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
      ) {
            return true;
      }

      @Override
      public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
      ) {
            String username = request.getPrincipal().getName();
            set(entityRepo.getUser(username));
      }

      @Override
      public OnlineStatus get(String username) {
            OnlineStatus status = onlineRepo.findById(username).orElse(new OnlineStatus(username, 0));
            status.setUser(entityRepo.getUser(username));
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
                        status.setUser(entityRepo.getUser(who));
                        return status;
                  })
                  .collect(Collectors.toList());

            return result.stream()
                  .filter(status -> !status.getUsername().equals(username))
                  .collect(Collectors.toList());
      }
}