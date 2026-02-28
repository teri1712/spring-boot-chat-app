package com.decade.practice.search.dto;

import com.decade.practice.users.api.UserInfo;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchingMessageHistoryResponse {

      private String chatId;

      private String roomName;
      private String content;
      private List<UserInfo> creators;
}
