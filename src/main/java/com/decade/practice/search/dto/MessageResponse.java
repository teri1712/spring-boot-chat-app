package com.decade.practice.search.dto;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {

    private String chatId;

    private Long sequenceNumber;
    private String content;

    private Instant createdAt;
}
