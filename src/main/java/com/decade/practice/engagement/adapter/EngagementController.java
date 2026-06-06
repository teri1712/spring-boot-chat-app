package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.domain.services.ChatCapacityReachedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/engagements")
@RequiredArgsConstructor
public class EngagementController {

    final EngagementService engagementService;

    @ExceptionHandler(ChatCapacityReachedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handle(ChatCapacityReachedException e) {
        log.warn("Chat capacity reached", e);
    }

    @Operation(summary = "Add participants to a chat",
        responses = {
            @ApiResponse(responseCode = "201", description = "Participants added"),
            @ApiResponse(responseCode = "404", description = "Chat not found", content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
            )),
            @ApiResponse(responseCode = "400", description = "Validation error, expect partner size from 1 to 100", content = @Content(
                examples = {@ExampleObject(ref = "#/components/examples/Validation")}
            ))
        }
    )
    @PostMapping("/{chatId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    void add(
        @Parameter(description = "Set of participant ids to be added, size must be between 1 and 100")
        @Size(max = 100, min = 1)
        @RequestParam("partner")
        Set<UUID> partners,

        @PathVariable String chatId
    ) {
        engagementService.add(chatId, partners);
    }

}
