package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.dto.ConversationResponse;
import com.decade.practice.inbox.dto.ConversationWithPartnerDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {MessageStateWithPartnerMapper.class}
)
public interface ConversationWithPartnerMapper {

    @Mapping(target = "roomName", expression = "java(convo.info().getName(lookUp))")
    @Mapping(target = "roomAvatar", expression = "java(convo.info().getAvatar(lookUp))")
    ConversationWithPartnerDto toDto(ConversationResponse convo, @Context PartnerLookUp lookUp);

    default List<ConversationWithPartnerDto> toDtos(List<ConversationResponse> convos, @Context PartnerLookUp lookUp) {
        return convos.stream()
            .map(cv -> toDto(cv, lookUp))
            .toList();
    }
}
