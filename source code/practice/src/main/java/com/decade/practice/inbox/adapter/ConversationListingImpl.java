package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.adapter.exception.MismatchHashException;
import com.decade.practice.inbox.application.ports.out.ConversationListing;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.HashValue;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class ConversationListingImpl implements ConversationListing {
      private final ConversationRepository conversations;

      @Override
      public List<ConversationView> findByAnchor(HashValue anchorHash, UUID ownerId, Pageable pageable) throws Throwable {
            Conversation anchor = conversations.findFirstByHash(anchorHash)
                      .orElseThrow((Supplier<Throwable>) MismatchHashException::new);
            return conversations.findByConversationId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(ownerId, anchor.getModifiedAt(), pageable);
      }

      @Override
      public List<ConversationView> findByModifiedAtLessThan(UUID ownerId, Instant modifiedAt, Pageable pageable) {
            return conversations.findByConversationId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(ownerId, modifiedAt, pageable);
      }
}
