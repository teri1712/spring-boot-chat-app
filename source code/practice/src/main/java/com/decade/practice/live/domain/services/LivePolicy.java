package com.decade.practice.live.domain.services;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.api.EngagementRule;
import com.decade.practice.engagement.api.RuleNotFoundException;
import com.decade.practice.live.domain.LiveJoiner;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LivePolicy {

      private final EngagementApi engagement;

      public void leave(LiveJoiner joiner) {
            EngagementRule participant = null;
            try {
                  participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
            } catch (RuleNotFoundException e) {

                  log.warn("Participant not found for chatId: {}, userId: {}", joiner.getChatId().value(), joiner.getUserId());
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            if (!participant.write()) {
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            joiner.leave();
      }

      public void join(LiveJoiner joiner) {
            EngagementRule participant = null;
            try {
                  participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
            } catch (RuleNotFoundException e) {

                  log.warn("Participant not found for chatId: {}, userId: {}", joiner.getChatId().value(), joiner.getUserId());
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            if (!participant.read()) {
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            joiner.join();
      }

      public void send(LiveJoiner joiner) {
            EngagementRule participant = null;
            try {
                  participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
            } catch (RuleNotFoundException e) {

                  log.warn("Participant not found for chatId: {}, userId: {}", joiner.getChatId().value(), joiner.getUserId());
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            if (!participant.write()) {
                  throw new AccessDeniedException("You are not allowed to perform this operation");
            }
            joiner.type();
      }
}
