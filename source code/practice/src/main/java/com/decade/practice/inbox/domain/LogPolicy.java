package com.decade.practice.inbox.domain;

import com.decade.practice.engagement.api.EngagementRule;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class LogPolicy {

      public void applyRead(EngagementRule engagementRule) {
            if (!engagementRule.read())
                  throw new AccessDeniedException("You are not allowed to read this chat events");
      }
}
