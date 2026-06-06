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
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextStateWithPartnerDto.class, name = "text"),
    @JsonSubTypes.Type(value = IconStateWithPartnerDto.class, name = "icon"),
    @JsonSubTypes.Type(value = ImageStateWithPartnerDto.class, name = "image"),
    @JsonSubTypes.Type(value = FileStateWithPartnerDto.class, name = "file"),
    @JsonSubTypes.Type(value = PreferenceStateWithPartnerDto.class, name = "preference"),
    @JsonSubTypes.Type(value = HelloGroupStateWithPartnerDto.class, name = "group"),
})
public class MessageStateWithPartnerDto {
    private Long sequenceNumber;
    private UUID postingId;
    private PartnerResponse sender;
    private String messageType;
    private String chatId;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<PartnerResponse> seenBy;
}
