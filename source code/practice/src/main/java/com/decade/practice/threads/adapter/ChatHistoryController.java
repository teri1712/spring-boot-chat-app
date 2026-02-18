package com.decade.practice.threads.adapter;

import com.decade.practice.threads.application.query.ChatHistoryService;
import com.decade.practice.threads.dto.ChatHistoryResponse;
import com.decade.practice.threads.dto.HistoryOffsetRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    // TODO: Adjust client and test
    @GetMapping("/me/chats")
    public List<ChatHistoryResponse> listChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam Optional<String> startAt,
            @RequestParam Optional<Long> hashValue
    ) {
        Optional<HistoryOffsetRequest> requestOptional = startAt.flatMap(s -> hashValue.map(h -> new HistoryOffsetRequest(s, h)));
        return chatHistoryService.listChat(userId, requestOptional);
    }
}