package com.decade.practice.inbox.dto.mapper;


import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.users.api.UserInfo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PartnerMapper.class, MessageMapper.class})
public abstract class InboxLogMapper {

      @Autowired
      protected MessageMapper messageMapper;

      @Mapping(target = "roomNameSnapshot", expression = "java(context.roomNameSnapshot())")
      @Mapping(target = "roomAvatarSnapshot", expression = "java(context.roomAvatarSnapshot())")
      @Mapping(target = "revisionNumber", expression = "java(context.chatHash())")
      @Mapping(target = "sequenceNumber", source = "inboxLog.sequenceId")
      @Mapping(target = "messageState", expression = "java(messageMapper.map(inboxLog.messageState(),context.userMap()))")
      public abstract InboxLogResponse map(InboxLogCreated inboxLog, @Context InboxContext context);

      @Mapping(target = "roomNameSnapshot", expression = "java(context.roomNameSnapshot())")
      @Mapping(target = "roomAvatarSnapshot", expression = "java(context.roomAvatarSnapshot())")
      @Mapping(target = "revisionNumber", expression = "java(context.chatHash())")
      @Mapping(target = "sequenceNumber", source = "inboxLog.sequenceId")
      @Mapping(target = "messageState", expression = "java(messageMapper.map(inboxLog.getMessageState(),context.userMap()))")
      public abstract InboxLogResponse map(InboxLog inboxLog, @Context InboxContext context);

      public List<InboxLogResponse> map(List<InboxLog> inboxLogs, InboxContext context) {
            return inboxLogs.stream().map(inboxLog -> map(inboxLog, context)).toList();
      }

      public record InboxContext(Map<UUID, UserInfo> userMap, String roomNameSnapshot, String roomAvatarSnapshot, Long chatHash) {
      }

}
