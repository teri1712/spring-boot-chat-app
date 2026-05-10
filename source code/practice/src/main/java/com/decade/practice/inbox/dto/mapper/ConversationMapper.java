package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.dto.ConversationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {MessageMapper.class})
public interface ConversationMapper {

    @Mapping(source = "c.room.chatId", target = "identifier")
    @Mapping(source = "c.conversation.hash.value", target = "revisionNumber")
    @Mapping(source = "c.conversation.recents", target = "recents")
    @Mapping(source = "c.conversation.modifiedAt", target = "modifiedAt")
    ConversationResponse map(ConversationView c, ConversationInfo info);

}
