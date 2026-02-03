package com.decade.practice.persistence.jpa.repositories;

import com.decade.practice.persistence.jpa.entities.Outbox;
import com.decade.practice.persistence.jpa.entities.OutboxStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @QueryHints({
            @QueryHint(
                    name = "jakarta.persistence.lock.timeout",
                    value = "-2"
            )
    })
    List<Outbox> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
