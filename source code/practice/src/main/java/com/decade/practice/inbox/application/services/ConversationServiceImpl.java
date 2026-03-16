package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.out.ConversationListing;
import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.application.query.ConversationService;
import com.decade.practice.inbox.domain.*;
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
      private final ConversationMapper conversationMapper;
      private final UserLookUp userLookUp;
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
            List<Conversation> convoList = convoViewList.stream().map(ConversationView::conversation).toList();
            List<Room> roomList = convoViewList.stream().map(ConversationView::room).toList();
            userLookUp.registerLookUp(aggregateAllNeededUsers(convoList));
            Map<String, ConversationInfo> roomLookUp = conversationInfoService.getInfo(userId, roomList);
            return conversationMapper.map(convoList, userLookUp, roomLookUp);
      }

      private static Set<UUID> aggregateAllNeededUsers(List<Conversation> convoList) {
            return convoList.stream()
                      .flatMap((Function<Conversation, Stream<MessageState>>)
                                conversation -> conversation.getRecents().stream())
                      .flatMap((Function<MessageState, Stream<UUID>>) messageState -> Stream.concat(messageState.getSeenByIds().stream(), Stream.of(messageState.getSenderId())))
                      .collect(Collectors.toSet());
      }
}
