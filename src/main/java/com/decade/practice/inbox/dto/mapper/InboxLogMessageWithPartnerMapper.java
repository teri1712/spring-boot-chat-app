package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.dto.InboxLogMessageWithPartnerDto;
import org.mapstruct.*;


@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {MessageStateWithPartnerMapper.class, PartnerMapper.class}
)
public interface InboxLogMessageWithPartnerMapper {
    @Mapping(target = "roomName", expression = "java(info.getName(lookUp))")
    @Mapping(target = "roomAvatar", expression = "java(info.getAvatar(lookUp))")
    @Mapping(target = "sender", source = "message.senderId")
    InboxLogMessageWithPartnerDto map(InboxLogMessage message, ConversationInfo info, @Context PartnerLookUp lookUp);
}
