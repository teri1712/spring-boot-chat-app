package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.dto.ConversationWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.ConversationWithPartnerMapper;
import lombok.AllArgsConstructor;
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

    // TODO: Adjust client endpoint
    @GetMapping("/conversations")
    public List<ConversationWithPartnerDto> list(
        @AuthenticationPrincipal(expression = "id") UUID userId,
        @RequestParam Optional<Long> anchorRevisionNumber
    ) throws Throwable {
        var convos = conversationService.list(userId, anchorRevisionNumber);
        Set<UUID> allUsers = conversationUserAggregator.aggregate(convos).collect(Collectors.toSet());
        return mapper.toDtos(convos, lookUpRegistry.registerLookUp(allUsers));
    }
}
