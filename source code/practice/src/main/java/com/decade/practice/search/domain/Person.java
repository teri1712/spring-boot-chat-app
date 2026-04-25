package com.decade.practice.search.domain;

import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "people")
public record People(
    @Id
    UUID id,
    String username,
    String name,
    String gender,
    String avatar
) {
}
