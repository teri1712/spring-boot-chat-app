package com.decade.practice.threads.application.ports.out;

import com.decade.practice.threads.domain.UserThread;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserThreadRepository extends JpaRepository<UserThread, UUID> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserThread> findById(UUID id);
}
