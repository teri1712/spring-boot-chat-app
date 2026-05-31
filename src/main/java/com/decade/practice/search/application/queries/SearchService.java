package com.decade.practice.search.application.queries;

import com.decade.practice.search.dto.MessageResponse;
import com.decade.practice.search.dto.PeopleResponse;

import java.util.List;
import java.util.UUID;

public interface SearchService {

    List<PeopleResponse> searchUsers(String query);

    List<MessageResponse> searchMessages(String chatId, UUID userId, String query);
}
