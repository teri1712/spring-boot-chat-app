package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Chat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    @Override
    Optional<Chat> findById(String s);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Override
    <S extends Chat> S save(S entity);
}