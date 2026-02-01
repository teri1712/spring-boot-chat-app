package com.decade.practice.application.usecases;

import com.decade.practice.dto.SearchResultDto;
import com.decade.practice.dto.UserResponse;

import java.util.List;

// TODO: Migrate to microservice
public interface SearchService {
    List<UserResponse> searchUsers(String query);

    List<SearchResultDto> searchMessages(String query);
}
