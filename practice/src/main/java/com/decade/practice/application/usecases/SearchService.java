package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.UserResponse;

import java.util.List;

public interface SearchService {
    List<UserResponse> searchUsers(String query);

    List<UserResponse> searchMessages(String query);
}
