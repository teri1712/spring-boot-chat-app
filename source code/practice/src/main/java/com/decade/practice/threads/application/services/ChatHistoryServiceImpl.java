package com.decade.practice.threads.application.services;

import com.decade.practice.threads.application.exception.MismatchHashException;
import com.decade.practice.threads.application.ports.out.ChatHistoryRepository;
import com.decade.practice.threads.application.query.ChatHistoryService;
import com.decade.practice.threads.domain.ChatHistory;
import com.decade.practice.threads.domain.ChatHistoryId;
import com.decade.practice.threads.domain.HashValue;
import com.decade.practice.threads.dto.ChatHistoryResponse;
import com.decade.practice.threads.dto.HistoryOffsetRequest;
import com.decade.practice.threads.dto.mapper.ChatHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository histories;
    private final ChatHistoryMapper historyMapper;


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public List<ChatHistoryResponse> listChat(UUID userId, Optional<HistoryOffsetRequest> offset) {
        PageRequest pageRequest = PageRequest.of(0, 20);
        if (offset.isEmpty()) {
            return histories.findByChatHistoryId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(userId, Instant.now().plusSeconds(60), pageRequest)
                    .map(historyMapper::toHistoryResponse)
                    .toList();
        }
        HistoryOffsetRequest offsetRequest = offset.get();
        ChatHistory startAt = histories.findById(new ChatHistoryId(offsetRequest.chatId(), userId)).orElseThrow();
        HashValue got = new HashValue(offsetRequest.hashValue());
        HashValue expected = startAt.getHash();
        if (!Objects.equals(got, expected)) {
            throw new MismatchHashException(expected, got);
        }
        return histories.findByChatHistoryId_OwnerIdAndModifiedAtLessThanOrderByModifiedAtDesc(userId, startAt.getModifiedAt(), pageRequest)
                .map(historyMapper::toHistoryResponse)
                .toList();
    }

}
