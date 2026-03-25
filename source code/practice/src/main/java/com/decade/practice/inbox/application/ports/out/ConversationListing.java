package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.HashValue;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ConversationListing {

      List<ConversationView> findByAnchor(HashValue anchor, UUID ownerId, Pageable pageable) throws Throwable;

      List<ConversationView> findByModifiedAtLessThan(UUID ownerId, Instant modifiedAt, Pageable pageable);

}
