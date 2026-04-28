package com.decade.practice.search.adapter;

import com.decade.practice.search.application.queries.SearchService;
import com.decade.practice.search.dto.MessageResponse;
import com.decade.practice.search.dto.PeopleResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/people")
    // TODO: Fix client endpoint
    public List<PeopleResponse> findUsers(
        @RequestParam String query
    ) {
        return searchService.searchUsers(query);
    }

    @GetMapping("/chat-histories/{chatId}")
    public List<MessageResponse> findMessages(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId,
        @RequestParam String query
    ) {
        return searchService.searchMessages(chatId, userId, query);
    }

}
