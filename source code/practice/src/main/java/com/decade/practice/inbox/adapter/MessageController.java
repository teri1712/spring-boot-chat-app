package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.dto.MessageStateWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.MessageStateWithPartnerMapper;
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
// TODO: refactor client
public class MessageController {

    private final MessageService messageService;
    private final LookUpRegistry lookUpRegistry;
    private final MessageStateWithPartnerMapper messageStateWithPartnerMapper;
    private final MessageStateUserAggregator messageAggregator;

    @GetMapping("/chats/{chatId}/messages")
    public List<MessageStateWithPartnerDto> listMessages(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId,
        @RequestParam Long anchorSequenceNumber
    ) throws EntityNotFoundException {
        log.debug("Fetching messages for chat: {} at anchor {} by user {}", chatId, anchorSequenceNumber, userId);
        var messages = messageService.findByChatAndSequenceLessThanEqual(chatId, userId, anchorSequenceNumber);
        Set<UUID> allUsers = messageAggregator.aggregate(messages).collect(Collectors.toSet());
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allUsers);
        return messageStateWithPartnerMapper.toDtos(messages, lookUp);
    }

}