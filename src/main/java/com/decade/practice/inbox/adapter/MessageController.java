package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.dto.MessageStateWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.MessageStateWithPartnerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;
    private final LookUpRegistry lookUpRegistry;
    private final MessageStateWithPartnerMapper messageStateWithPartnerMapper;
    private final MessageStateUserAggregator messageAggregator;

    @Operation(summary = "Get messages for a chat",
        responses = {
            @ApiResponse(responseCode = "200", description = "The list of messages ordered by sequence number in desc order, including the anchor"),
            @ApiResponse(responseCode = "400", description = "Validation failure", content = @Content(
                examples = {@ExampleObject(ref = "#/components/examples/Validation")}
            ))
        }
    )
    @GetMapping("/chats/{chatId}/messages")
    public List<MessageStateWithPartnerDto> listMessages(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId,
        @Parameter(description = "sequence number of the anchor message")
        @RequestParam Long anchorSequenceNumber
    ) throws EntityNotFoundException {
        log.debug("Fetching messages for chat: {} at anchor {} by user {}", chatId, anchorSequenceNumber, userId);
        var messages = messageService.findByChatAndSequenceLessThanEqual(chatId, userId, anchorSequenceNumber);
        Set<UUID> allUsers = messageAggregator.aggregate(messages).collect(Collectors.toSet());
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allUsers);
        return messageStateWithPartnerMapper.toDtos(messages, lookUp);
    }

}