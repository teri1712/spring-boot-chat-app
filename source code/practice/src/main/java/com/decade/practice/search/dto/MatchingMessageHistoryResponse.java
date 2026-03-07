package com.decade.practice.search.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingMessageHistoryResponse {

      private String chatId;

      private String roomName;
      private String content;
}
