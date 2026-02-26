package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.out.ConversationListing;
import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.HashValue;
import com.decade.practice.inbox.domain.MessagePreview;
import com.decade.practice.inbox.dto.ConversationResponse;
import com.decade.practice.inbox.dto.mapper.ChatHistoryMapper;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

      private final ConversationListing conversationListing;
      private final ChatHistoryMapper historyMapper;
      private final UserApi userApi;


      @Override
      @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
      public List<ConversationResponse> list(UUID userId, Optional<Long> anchor) throws Throwable {
            PageRequest pageRequest = PageRequest.of(0, 20);
            if (anchor.isEmpty()) {
                  List<Conversation> historyList = conversationListing.findByModifiedAtLessThan(userId, Instant.now().plusSeconds(60), pageRequest);
                  Map<UUID, UserInfo> allNeededUsers = userApi.getUserInfo(aggregateAllNeededUsers(historyList));
                  return historyMapper.map(historyList, allNeededUsers);

            }
            List<Conversation> historyList = conversationListing.findByModifiedAtLessThan(new HashValue(anchor.get()), userId, pageRequest);
            Map<UUID, UserInfo> allNeededUsers = userApi.getUserInfo(aggregateAllNeededUsers(historyList));
            return historyMapper.map(historyList, allNeededUsers);
      }

      private static Set<UUID> aggregateAllNeededUsers(List<Conversation> historyList) {
            Set<UUID> allNeededUsers = historyList.stream()
                      .flatMap((Function<Conversation, Stream<MessagePreview>>)
                                history -> history.getMessagePreviews().stream())
                      .map(MessagePreview::sentBy)
                      .collect(Collectors.toSet());
            allNeededUsers.addAll(historyList.stream()
                      .flatMap((Function<Conversation, Stream<UUID>>)
                                history -> history.getSeenBy().stream())
                      .collect(Collectors.toSet()));
            return allNeededUsers;
      }

}
