package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.domain.ConversationId;
import com.decade.practice.inbox.domain.SeenPointer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeenPointerRepository extends JpaRepository<SeenPointer, ConversationId> {
}
