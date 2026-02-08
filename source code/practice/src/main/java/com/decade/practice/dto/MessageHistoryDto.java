package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageHistoryDto {

    private UUID id;
    private ChatCreators chatCreators;

    private String partnerName;
    private String content;
}
