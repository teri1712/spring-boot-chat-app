package com.decade.practice.search.adapter;

import com.decade.practice.search.application.queries.SearchService;
import com.decade.practice.search.dto.MessageResponse;
import com.decade.practice.search.dto.PeopleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/people")
    @Operation(summary = "Search people by name, keywords",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of matched users")
        })
    public List<PeopleResponse> findUsers(
        @RequestParam String query
    ) {
        return searchService.searchUsers(query);
    }

    @GetMapping("/chat-histories/{chatId}")
    @Operation(
        summary = "Find messages in a chat",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of matched messages"),
            @ApiResponse(responseCode = "404", description = "Chat not found", content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
            ))
        }
    )
    public List<MessageResponse> findMessages(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId,
        @RequestParam String query
    ) {
        return searchService.searchMessages(chatId, userId, query);
    }

}
