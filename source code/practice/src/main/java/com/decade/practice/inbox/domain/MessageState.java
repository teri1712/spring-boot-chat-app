package com.decade.practice.inbox.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@JsonTypeInfo(
          use = JsonTypeInfo.Id.NAME,
          include = JsonTypeInfo.As.PROPERTY,
          property = "type"
)

// fuck jackson
@JsonSubTypes({
          @JsonSubTypes.Type(value = TextState.class, name = "text"),
          @JsonSubTypes.Type(value = IconState.class, name = "icon"),
          @JsonSubTypes.Type(value = ImageState.class, name = "image"),
          @JsonSubTypes.Type(value = FileState.class, name = "file"),
          @JsonSubTypes.Type(value = PreferenceState.class, name = "preference"),
})
@SuperBuilder
@Getter

// TODO: Will be migrated to runtime state resolution later
public abstract class MessageState {

      private Long sequenceId;
      private UUID postingId;
      private UUID senderId;
      private String messageType;
      private String chatId;
      private Instant createdAt;
      private Instant updatedAt;
      private Set<UUID> seenByIds;

}
