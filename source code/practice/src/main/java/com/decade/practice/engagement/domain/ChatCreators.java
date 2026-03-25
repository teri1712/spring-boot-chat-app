package com.decade.practice.engagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public record ChatCreators(
          UUID callerId,
          @JdbcTypeCode(SqlTypes.JSON)
          @Column(columnDefinition = "jsonb")
          Set<UUID> partners
) {

      public ChatCreators {
            partners = partners.stream()
                      .filter(participant -> !participant.equals(callerId))
                      .collect(Collectors.toSet());
      }

      public Set<UUID> getMembers() {
            return Stream.concat(Stream.of(callerId), partners.stream())
                      .collect(Collectors.toSet());
      }
}