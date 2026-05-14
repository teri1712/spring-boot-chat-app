package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ImageMapper.class, PartnerMapper.class}
)
public abstract class MessageStateWithPartnerMapper {

    @SubclassMapping(source = TextStateResponse.class, target = TextStateWithPartnerDto.class)
    @SubclassMapping(source = IconStateResponse.class, target = IconStateWithPartnerDto.class)
    @SubclassMapping(source = ImageStateResponse.class, target = ImageStateWithPartnerDto.class)
    @SubclassMapping(source = FileStateResponse.class, target = FileStateWithPartnerDto.class)
    @SubclassMapping(source = PreferenceStateResponse.class, target = PreferenceStateWithPartnerDto.class)
    @SubclassMapping(source = HelloGroupStateResponse.class, target = HelloGroupStateWithPartnerDto.class)
    @Mapping(target = "sequenceNumber", source = "sequenceNumber")
    @Mapping(target = "sender", source = "senderId")
    @Mapping(target = "seenBy", source = "seenByIds")
    public abstract MessageStateWithPartnerDto toDto(MessageStateResponse message, @Context PartnerLookUp lookUp);

    public List<MessageStateWithPartnerDto> toDtos(List<MessageStateResponse> messages, @Context PartnerLookUp lookUp) {
        return messages.stream().map(m -> toDto(m, lookUp)).toList();
    }

}
