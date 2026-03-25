package com.decade.practice.inbox.dto.mapper;


import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.application.ports.out.projection.LogView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.dto.InboxLogResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {MessageMapper.class})
public abstract class InboxLogMapper {

      @Mapping(target = "roomName", expression = "java(info.name())")
      @Mapping(target = "roomAvatar", expression = "java(info.avatar())")
      @Mapping(target = "revisionNumber", expression = "java(conversation.getHash().value())")
      @Mapping(target = "sequenceNumber", source = "inboxLog.sequenceId")
      @Mapping(target = "sender", source = "inboxLog.senderId")
      public abstract InboxLogMessage map(InboxLog inboxLog,
                                          @Context PartnerLookUp lookUp,
                                          @Context Conversation conversation,
                                          @Context ConversationInfo info);

      @Mapping(target = "roomName", expression = "java(infoMap.get(inboxLog.log().getChatId()).name())")
      @Mapping(target = "roomAvatar", expression = "java(infoMap.get(inboxLog.log().getChatId()).avatar())")
      @Mapping(target = "revisionNumber", source = "conversationView.conversation.hash.value")
      @Mapping(target = "sequenceNumber", source = "log.sequenceId")
      @Mapping(target = "messageState", source = "log.messageState")
      @Mapping(target = "sender", source = "log.senderId")
      @Mapping(target = "chatId", source = "log.chatId")
      @Mapping(target = "ownerId", source = "log.ownerId")
      @Mapping(target = "action", source = "log.action")
      public abstract InboxLogResponse map(LogView inboxLog, @Context PartnerLookUp lookUp, @Context Map<String, ConversationInfo> infoMap);

      public List<InboxLogResponse> map(List<LogView> inboxLogs, PartnerLookUp lookUp, Map<String, ConversationInfo> infoMap) {
            return inboxLogs.stream().map(inboxLog -> map(inboxLog, lookUp, infoMap)).toList();
      }

}
