package com.decade.practice.search.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingMessageHistoryResponse {

    private UUID id;
    private String chatId;

    private String roomName;
    private String content;
}
