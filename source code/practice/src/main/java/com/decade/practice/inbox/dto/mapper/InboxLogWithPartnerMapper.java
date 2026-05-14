package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.inbox.dto.InboxLogWithPartnerDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {MessageStateWithPartnerMapper.class, PartnerMapper.class}
)
public interface InboxLogWithPartnerMapper {

    @Mapping(target = "sequenceNumber", source = "log.sequenceNumber")
    @Mapping(target = "roomName", expression = "java(log.info().getName(lookUp))")
    @Mapping(target = "roomAvatar", expression = "java(log.info().getAvatar(lookUp))")
    @Mapping(target = "revisionNumber", source = "log.revisionNumber")
    @Mapping(target = "sender", source = "log.senderId")
    @Mapping(target = "ownerId", source = "log.ownerId")
    @Mapping(target = "action", source = "log.action")
    @Mapping(target = "messageState", source = "log.messageState")
    InboxLogWithPartnerDto toDto(InboxLogResponse log, @Context PartnerLookUp lookUp);

    default List<InboxLogWithPartnerDto> toDtos(List<InboxLogResponse> logs, @Context PartnerLookUp lookUp) {
        return logs.stream().map(lv -> toDto(lv, lookUp)).toList();
    }
}
