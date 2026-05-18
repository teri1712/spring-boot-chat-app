package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.ConversationListing;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.HashValue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ConversationListingImpl implements ConversationListing {
    private final ConversationRepository conversations;

    @Override
    public List<ConversationView> findByAnchor(HashValue anchorHash, UUID ownerId, Pageable pageable) throws Throwable {
        Conversation anchor = conversations.findFirstByHash(anchorHash).orElseThrow();
        return conversations.findByOwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(ownerId, anchor.getModifiedAt(), pageable);
    }

    @Override
    public List<ConversationView> findByModifiedAtLessThan(UUID ownerId, Instant modifiedAt, Pageable pageable) {
        log.info("YEP {}", ownerId);
        log.info("YEP {}", modifiedAt);

        return conversations.findByOwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(ownerId, modifiedAt, pageable);
    }
}
