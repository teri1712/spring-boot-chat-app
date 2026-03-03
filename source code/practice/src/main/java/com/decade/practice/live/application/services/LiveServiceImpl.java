package com.decade.practice.live.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.engagement.api.WritePolicy;
import com.decade.practice.live.application.ports.in.LiveService;
import com.decade.practice.live.application.ports.out.JoinerRepository;
import com.decade.practice.live.domain.LiveJoiner;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LiveServiceImpl implements LiveService {
      private final JoinerRepository joiners;

      @Override
      @ReadPolicy
      public void join(String chatId, UUID userId, String avatar) {
            LiveJoiner joiner = new LiveJoiner(chatId, userId, avatar);
            joiner.join();
            joiners.save(joiner);
      }

      @Override
      @WritePolicy
      public void leave(String chatId, UUID userId, String avatar) {
            String key = LiveJoiner.determineKey(userId, chatId);
            LiveJoiner joiner = joiners.findById(key)
                      .orElse(new LiveJoiner(chatId, userId, avatar));
            joiner.leave();
            joiners.delete(joiner);
      }

      @Override
      @WritePolicy
      public void send(String chatId, UUID userId, String avatar) {
            String key = LiveJoiner.determineKey(userId, chatId);
            LiveJoiner joiner = joiners.findById(key)
                      .orElse(new LiveJoiner(chatId, userId, avatar));

            joiner.type();
            joiners.save(joiner);
      }
}
