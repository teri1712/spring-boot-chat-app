package com.decade.practice.inbox.dto.mapper;


import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.application.ports.out.projection.LogView;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.dto.InboxLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {MessageMapper.class})
public abstract class InboxLogMapper {

    @Mapping(target = "revisionNumber", source = "conversationView.conversation.hash.value")
    @Mapping(target = "sequenceNumber", source = "inboxLog.sequenceId")
    @Mapping(target = "messageState", source = "inboxLog.messageState")
    @Mapping(target = "chatId", source = "conversationView.room.chatId")
    public abstract InboxLogMessage map(InboxLog inboxLog,
                                        ConversationView conversationView,
                                        ConversationInfo info);

    @Mapping(target = "revisionNumber", source = "logView.conversationView.conversation.hash.value")
    @Mapping(target = "sequenceNumber", source = "logView.log.sequenceId")
    @Mapping(target = "messageState", source = "logView.log.messageState")
    @Mapping(target = "senderId", source = "logView.log.senderId")
    @Mapping(target = "chatId", source = "logView.conversationView.room.chatId")
    @Mapping(target = "ownerId", source = "logView.log.ownerId")
    @Mapping(target = "action", source = "logView.log.action")
    public abstract InboxLogResponse map(LogView logView, ConversationInfo info);

}
