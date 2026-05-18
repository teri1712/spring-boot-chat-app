package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.dto.ConversationWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.ConversationWithPartnerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final ConversationWithPartnerMapper mapper;
    private final ConversationUserAggregator conversationUserAggregator;
    private final LookUpRegistry lookUpRegistry;


    @Operation(summary = "Get or intialize a direct chat",
        responses = {
            @ApiResponse(responseCode = "200", description = "The list conversation ordered by modification time, and starting from the anchor conversation"),
            @ApiResponse(responseCode = "404", description = "Revision might not be found",
                content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
                ))
        }
    )
    @GetMapping("/conversations")
    public List<ConversationWithPartnerDto> list(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @Parameter(description = "revision number of the anchor chat")
        @RequestParam Optional<Long> anchorRevisionNumber
    ) throws Throwable {
        var convos = conversationService.list(userId, anchorRevisionNumber);
        Set<UUID> allUsers = conversationUserAggregator.aggregate(convos).collect(Collectors.toSet());
        return mapper.toDtos(convos, lookUpRegistry.registerLookUp(allUsers));
    }
}
