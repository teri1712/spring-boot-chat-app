package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.HashValue;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ConversationListing {
      List<Conversation> findByModifiedAtLessThan(HashValue anchor, UUID ownerId, Pageable pageable) throws Throwable;

      List<Conversation> findByModifiedAtLessThan(UUID ownerId, Instant modifiedAt, Pageable pageable);

}
