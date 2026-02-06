package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    @SequenceGenerator(name = "outbox_seq", sequenceName = "outbox_seq", allocationSize = 50)
    private Long id;

    private String key;
    private String topic;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "outbox_status")
    private OutboxStatus status = OutboxStatus.PENDING;
}
