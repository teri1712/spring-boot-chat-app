package com.decade.practice.inbox.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.application.ports.out.projection.LogView;
import com.decade.practice.inbox.application.query.LogService;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import com.decade.practice.inbox.utils.LogUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
      private final InboxLogMapper mapper;
      private final ConversationInfoService conversationInfoService;
      private final LookUpRegistry lookUpRegistry;

      @Override
      @ReadPolicy
      public List<InboxLogResponse> findByChatAndSequenceGreaterThanEqual(String chatId, UUID userId, Long anchorSequenceNumber) {
            log.trace("Finding events for ownerId '{}' and chat '{}'", userId, chatId);
            List<LogView> logList = logs.findByOwnerIdAndChatIdAndSequenceIdGreaterThanEqual(userId, chatId, anchorSequenceNumber, LogUtils.SEQUENCE_ASC_PAGE);
            return map(userId, logList);
      }

      private List<InboxLogResponse> map(UUID userId, List<LogView> logList) {
            Stream<UUID> allLogUsers = logList.stream().flatMap(new Function<LogView, Stream<UUID>>() {
                  @Override
                  public Stream<UUID> apply(LogView logWithConversation) {
                        InboxLog log = logWithConversation.log();
                        return log.getMessageState().getAllPartners();
                  }
            });

            List<Room> roomList = logList.stream().map(LogView::conversationView).map(ConversationView::room).toList();

            Stream<UUID> allRoomUsers = roomList.stream().flatMap(new Function<Room, Stream<UUID>>() {
                  @Override
                  public Stream<UUID> apply(Room room) {
                        return Stream.concat(room.getRepresentatives().stream(), Stream.of(room.getCreator()));
                  }
            });

            PartnerLookUp lookUp = lookUpRegistry.registerLookUp(Stream.concat(allLogUsers, allRoomUsers).collect(Collectors.toSet()));

            return mapper.map(logList, lookUp, conversationInfoService.getInfo(userId, roomList, lookUp));
      }

      @Override
      public List<InboxLogResponse> findBySequenceGreaterThanEqual(UUID userId, Long anchorSequenceNumber) {
            log.trace("Finding events for ownerId '{}'", userId);
            List<LogView> logList = logs.findByOwnerIdAndSequenceIdGreaterThanEqual(userId, anchorSequenceNumber, LogUtils.SEQUENCE_ASC_PAGE);
            return map(userId, logList);
      }
}
