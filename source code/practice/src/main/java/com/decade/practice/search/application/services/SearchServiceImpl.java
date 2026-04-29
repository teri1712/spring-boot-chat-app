package com.decade.practice.search.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.search.application.ports.out.MessageHistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.application.queries.SearchService;
import com.decade.practice.search.dto.MessageResponse;
import com.decade.practice.search.dto.PeopleResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final MessageHistoryRepository messages;
    private final PeopleRepository people;

    @Override
    public List<PeopleResponse> searchUsers(String string) {
        return people.findPeople(string).stream()
            .map(person -> new PeopleResponse(
                person.userId(),
                person.username(),
                person.name(),
                person.avatar()
            )).toList();
    }

    @Override
    @ReadPolicy
    public List<MessageResponse> searchMessages(String chatId, UUID userId, String string) {
        return messages.findByChatIdAndContent(chatId, string).stream()
            .map(history -> MessageResponse.builder()
                .chatId(history.chatId())
                .sequenceNumber(history.sequenceNumber())
                .content(history.content())
                .createdAt(history.createdAt())
                .build()).toList();
    }

}
