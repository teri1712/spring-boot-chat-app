package com.decade.practice.inbox.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.projection.LogWithConversation;
import com.decade.practice.inbox.application.query.LogService;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.ConversationId;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import com.decade.practice.inbox.utils.LogUtils;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class LogServiceImpl implements LogService {

      private final LogRepository logs;
      private final ConversationRepository conversations;
      private final InboxLogMapper mapper;
      private final UserApi userApi;

      @Override
      @ReadPolicy
      public List<InboxLogResponse> findByChatAndSequenceGreaterThanEqual(String chatId, UUID userId, Long anchorSequenceNumber) {
            log.trace("Finding events for ownerId '{}' and chat '{}'", userId, chatId);
            List<InboxLog> logList = logs.findByOwnerIdAndChatIdAndSequenceIdGreaterThanEqual(userId, chatId, anchorSequenceNumber, LogUtils.SEQUENCE_LESS_THAN_EQUAL);
            Map<UUID, UserInfo> userMap = userApi.getUserInfo(
                      logList.stream()
                                .map(InboxLog::getSenderId)
                                .collect(Collectors.toSet()));
            Conversation conversation = conversations.findById(new ConversationId(chatId, userId)).orElseThrow();
            return mapper.map(logList, new InboxLogMapper.InboxContext(userId, userMap, conversation.getName(), conversation.getAvatar(), conversation.getHash().value()));
      }

      @Override
      public List<InboxLogResponse> findBySequenceGreaterThanEqual(UUID userId, Long anchorSequenceNumber) {
            log.trace("Finding events for ownerId '{}'", userId);
            List<LogWithConversation> logList = logs.findByOwnerIdAndSequenceIdGreaterThanEqual(userId, anchorSequenceNumber, LogUtils.SEQUENCE_LESS_THAN_EQUAL);
            Map<UUID, UserInfo> userMap = userApi.getUserInfo(logList.stream().flatMap(new Function<LogWithConversation, Stream<UUID>>() {
                  @Override
                  public Stream<UUID> apply(LogWithConversation logWithConversation) {
                        InboxLog log = logWithConversation.log();
                        return Stream.concat(
                                  log.getMessageState().getSeenByIds().stream(),
                                  Stream.of(log.getSenderId())
                        ).distinct();
                  }
            }).collect(Collectors.toSet()));
            return logList.stream().map(new Function<LogWithConversation, InboxLogResponse>() {

                  @Override
                  public InboxLogResponse apply(LogWithConversation logWithConversation) {
                        return mapper.map(logWithConversation.log(), new InboxLogMapper.InboxContext(userId, userMap, logWithConversation.conversation().getName(), logWithConversation.conversation().getAvatar(), logWithConversation.conversation().getHash().value()));
                  }
            }).toList();
      }
}
