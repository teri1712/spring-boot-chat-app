package com.decade.practice.engagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "receipt_type")
public abstract class Receipt extends AbstractAggregateRoot<Receipt> {

    @Id
    private UUID idempotentKey;

    private String chatId;
    private UUID senderId;
    private Instant createdAt;

    public Receipt(UUID idempotentKey, String chatId, UUID senderId) {
        this.idempotentKey = idempotentKey;
        this.chatId = chatId;
        this.senderId = senderId;
        this.createdAt = Instant.now();
    }

    @Version
    private Integer version;

    protected Receipt() {

    }

    public abstract void place();
}
