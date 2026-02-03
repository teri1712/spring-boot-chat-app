package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.redis.TypeEvent;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeEventDto {

    private UUID from;
    private ChatIdentifier chat;
    private Instant time = Instant.now();

    private String key;

    public static TypeEventDto from(TypeEvent event) {
        return TypeEventDto.builder()
                .from(event.getFrom())
                .chat(event.getChat())
                .key(event.getKey())
                .build();
    }
}