package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.query.LogService;
import com.decade.practice.inbox.dto.InboxLogWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.InboxLogWithPartnerMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@AllArgsConstructor
public class LogController {

    private final LogService logService;
    private final LookUpRegistry lookUpRegistry;
    private final InboxLogWithPartnerMapper inboxLogWithPartnerMapper;
    private final LogAggregator logAggregator;

    // TODO: Adjust client to new endpoint
    @GetMapping("/chats/{chatId}/logs")
    List<InboxLogWithPartnerDto> listLog(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @PathVariable String chatId,
        @RequestParam Long anchorSequenceNumber
    ) throws EntityNotFoundException {
        var logs = logService.findByChatAndSequenceGreaterThanEqual(chatId, userId, anchorSequenceNumber);
        Set<UUID> allUsers = logAggregator.aggregate(logs)
            .collect(Collectors.toSet());
        return inboxLogWithPartnerMapper.toDtos(logs, lookUpRegistry.registerLookUp(allUsers));
    }

    // TODO: Adjust client to last currentState
    @GetMapping("/users/me/logs")
    List<InboxLogWithPartnerDto> listLog(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @RequestParam Long anchorSequenceNumber
    ) throws EntityNotFoundException {

        var logs = logService.findBySequenceGreaterThanEqual(userId, anchorSequenceNumber);
        Set<UUID> allUsers = logAggregator.aggregate(logs).collect(Collectors.toSet());
        return inboxLogWithPartnerMapper.toDtos(logs, lookUpRegistry.registerLookUp(allUsers));
    }
}
