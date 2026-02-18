package com.decade.practice.threads.domain;

import com.decade.practice.threads.domain.events.ThreadIncremented;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.UUID;

@Entity
@Getter
public class UserThread extends AbstractAggregateRoot<UserThread> {

    @Id
    private UUID userId;

    @Version
    private Integer version;

    protected UserThread() {
    }

    public UserThread(@NotNull UUID userId) {
        this.userId = userId;
    }

    @Embedded
    @Getter(AccessLevel.PRIVATE)
    private EventSequence eventSequence = new EventSequence();

    public void increment(UUID eventId) {
        Integer nextVersion = eventSequence.increment(eventId);
        registerEvent(new ThreadIncremented(eventId, nextVersion));
    }

}
