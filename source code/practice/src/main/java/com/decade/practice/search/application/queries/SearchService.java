package com.decade.practice.search.application.queries;

import com.decade.practice.search.dto.MatchingMessageHistoryResponse;
import com.decade.practice.search.dto.MatchingUserResponse;

import java.util.List;
import java.util.UUID;

public interface SearchService {

      List<MatchingUserResponse> searchUsers(String query);

      List<MatchingMessageHistoryResponse> searchMessages(UUID owner, String query);
}
