package com.decade.practice.search.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "people")
public record Person(
    @Id
    Long id,
    @Column("user_id")
    UUID userId,
    String username,
    String name,
    String gender,
    String avatar
) {
}
