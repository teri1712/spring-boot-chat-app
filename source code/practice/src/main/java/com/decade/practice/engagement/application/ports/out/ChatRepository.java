package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Chat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

      @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
      @Query("select c from Chat c where c.identifier = :id")
      Optional<Chat> findByIdIncrementVersion(String id);
}