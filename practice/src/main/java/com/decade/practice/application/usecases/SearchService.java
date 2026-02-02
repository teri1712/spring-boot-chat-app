package com.decade.practice.application.usecases;

import com.decade.practice.dto.MessageHistoryDto;
import com.decade.practice.dto.UserResponse;

import java.util.List;
import java.util.UUID;

// TODO: Migrate to microservice
public interface SearchService {
    List<UserResponse> searchUsers(String query);

    List<MessageHistoryDto> searchMessages(UUID owner, String query);
}
