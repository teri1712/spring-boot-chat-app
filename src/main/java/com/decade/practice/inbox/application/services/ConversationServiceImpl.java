package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.out.ConversationListing;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.domain.HashValue;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.ConversationResponse;
import com.decade.practice.inbox.dto.mapper.ConversationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationListing conversationListing;
    private final ConversationMapper mapper;
    private final ConversationInfoService conversationInfoService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public List<ConversationResponse> list(UUID userId, Optional<Long> anchorRevisionNumber) throws Throwable {
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        List<ConversationView> convoViewList;
        if (anchorRevisionNumber.isEmpty()) {
            convoViewList = conversationListing.findByModifiedAtLessThan(userId, Instant.now().plusSeconds(60), pageRequest);
        } else {
            convoViewList = conversationListing.findByAnchor(new HashValue(anchorRevisionNumber.get()), userId, pageRequest);
        }
        return convoViewList.stream().map(new Function<ConversationView, ConversationResponse>() {
            @Override
            public ConversationResponse apply(ConversationView conversationView) {
                return mapper.map(conversationView, conversationInfoService.getInfo(userId, conversationView.room()));
            }
        }).toList();
    }
}
