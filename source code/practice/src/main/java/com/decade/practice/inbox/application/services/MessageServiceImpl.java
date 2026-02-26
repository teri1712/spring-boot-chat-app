package com.decade.practice.inbox.application.services;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.domain.Message;
import com.decade.practice.inbox.dto.MessageStateResponse;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import com.decade.practice.inbox.utils.LogUtils;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

      private final MessageRepository messages;
      private final UserApi userApi;
      private final InboxLogMapper inboxLogMapper;

      @Override
      public List<MessageStateResponse> findByChatAndSequenceLessThanEqual(UUID owner, String chatId, Long anchorSequenceId) {

            if (anchorSequenceId == null) {
                  anchorSequenceId = Long.MAX_VALUE;
            }

            List<Message> messageList = messages.findByChatIdAndSequenceIdLessThanEqual(chatId, anchorSequenceId, LogUtils.SEQUENCE_LESS_THAN_EQUAL);
            Map<UUID, UserInfo> userMap = userApi.getUserInfo(messageList.stream().flatMap(new Function<Message, Stream<UUID>>() {
                  @Override
                  public Stream<UUID> apply(Message message) {
                        return Stream.concat(message.getSeenPointers().keySet().stream(), Stream.of(message.getSenderId()));
                  }
            }).collect(Collectors.toSet()));

            return messageList.stream().map(message ->
                                inboxLogMapper.map(message.getState(), new InboxLogMapper.InboxContext(userMap, null, null)))
                      .toList();
      }
}
