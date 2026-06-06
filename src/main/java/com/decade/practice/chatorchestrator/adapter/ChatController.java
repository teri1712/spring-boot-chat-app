package com.decade.practice.chatorchestrator.adapter;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.chatorchestrator.application.ports.in.CreateGroupChatCommand;
import com.decade.practice.chatorchestrator.dto.ChatResponse;
import com.decade.practice.chatorchestrator.dto.DirectChatResponse;
import com.decade.practice.engagement.api.ChatIdentifierUniqueException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @ExceptionHandler(ChatIdentifierUniqueException.class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChatResponse handle(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        ChatIdentifierUniqueException e) {
        String chatId = e.getId();
        log.debug("Concurrent insert encountered for chat: {}", chatId, e);
        return chatService.find(chatId, userId);
    }


    @Operation(summary = "Get details of a chat",
        responses = {
            @ApiResponse(responseCode = "200", description = "The chat"),
            @ApiResponse(responseCode = "404", description = "Chat not found",
                content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
                ))
        }
    )
    @GetMapping("/chats/{chatId}")
    public ChatResponse getChat(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId
    ) {
        return chatService.find(chatId, userId);
    }


    @Operation(summary = "Get or intialize a direct chat",
        responses = {
            @ApiResponse(responseCode = "200", description = "Return a direct mapping that contains chat id and whether the chat is newly created"),
            @ApiResponse(responseCode = "404", description = "Partner not found not found",
                content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
                ))
        }
    )
    @PutMapping("/direct-chats/{partnerId}")
    public ResponseEntity<DirectChatResponse> getChat(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable UUID partnerId
    ) {
        DirectChatResponse directChat = chatService.getDirect(userId, partnerId);
        HttpStatus status = directChat.newly() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(directChat);
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Get or intialize a direct chat",
        responses = {
            @ApiResponse(responseCode = "201", description = "Group is successfully created"),
            @ApiResponse(responseCode = "400", description = "Partner size vaidation failure",
                content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {@ExampleObject(ref = "#/components/examples/NotFound")}
                ))
        }
    )
    public ChatResponse createGroup(
        @AuthenticationPrincipal(expression = "id") UUID userId,

        @Parameter(description = "List of partner ids, size must be between 2 to 50")
        @Size(min = 2, max = 50)
        @RequestParam(name = "partnerId") Set<UUID> partnerIds,

        @Parameter(description = "Name of the group chat")
        @RequestParam String roomName
    ) {
        return chatService.createGroup(new CreateGroupChatCommand(userId, partnerIds, roomName));
    }
}