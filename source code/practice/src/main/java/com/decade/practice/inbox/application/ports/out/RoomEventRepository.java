package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.engagement.api.WritePolicy;
import com.decade.practice.inbox.domain.RoomEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface RoomEventRepository extends JpaRepository<RoomEvent, UUID> {

      @WritePolicy
      default void saveAndFlush(String chatId, UUID userId, RoomEvent entity) {
            saveAndFlush(entity);
      }
}