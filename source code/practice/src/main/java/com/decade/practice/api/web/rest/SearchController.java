package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.dto.MessageHistoryDto;
import com.decade.practice.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/users")
    public List<UserResponse> findUsers(
            @RequestParam() String query
    ) {
        return searchService.searchUsers(query);
    }


    @GetMapping("/me/history/messages")
    public List<MessageHistoryDto> findMessages(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam() String query
    ) {
        return searchService.searchMessages(userId, query);
    }

}
