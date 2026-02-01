package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {

    private UUID id;
    private ChatIdentifier chatIdentifier;

    private String partnerName;
    private String content;
}
