package com.decade.practice.inbox.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Jacksonized
@Getter

@JsonTypeInfo(
          use = JsonTypeInfo.Id.NAME,
          include = JsonTypeInfo.As.PROPERTY,
          property = "type"
)

// fuck jackson
@JsonSubTypes({
          @JsonSubTypes.Type(value = TextStateResponse.class, name = "text"),
          @JsonSubTypes.Type(value = IconStateResponse.class, name = "icon"),
          @JsonSubTypes.Type(value = ImageStateResponse.class, name = "image"),
          @JsonSubTypes.Type(value = FileStateResponse.class, name = "file"),
          @JsonSubTypes.Type(value = PreferenceStateResponse.class, name = "preference"),
})
public class MessageStateResponse {

      private Long sequenceNumber;
      private UUID engagementId;
      private PartnerResponse sender;
      private String messageType;
      private String chatId;
      private Instant createdAt;
      private Instant updatedAt;
      private Set<PartnerResponse> seenBy;

}
