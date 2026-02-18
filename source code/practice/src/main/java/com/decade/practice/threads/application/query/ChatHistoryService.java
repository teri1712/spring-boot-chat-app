package com.decade.practice.threads.application.query;

import com.decade.practice.threads.dto.ChatHistoryResponse;
import com.decade.practice.threads.dto.HistoryOffsetRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatHistoryService {

    List<ChatHistoryResponse> listChat(UUID userId, Optional<@Valid HistoryOffsetRequest> offset);

}