package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.dto.*;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ImageMapper.class}
)
public abstract class MessageStateWithPartnerMapper {

    @SubclassMapping(source = TextState.class, target = TextStateWithPartnerDto.class)
    @SubclassMapping(source = IconState.class, target = IconStateWithPartnerDto.class)
    @SubclassMapping(source = ImageState.class, target = ImageStateWithPartnerDto.class)
    @SubclassMapping(source = FileState.class, target = FileStateWithPartnerDto.class)
    @SubclassMapping(source = PreferenceState.class, target = PreferenceStateWithPartnerDto.class)
    @SubclassMapping(source = HelloGroupState.class, target = HelloGroupStateWithPartnerDto.class)
    @Mapping(target = "sequenceNumber", source = "sequenceNumber")
    @Mapping(target = "sender", source = "getSenderId")
    @Mapping(target = "seenBy", source = "getSeenByIds")
    public abstract MessageStateWithPartnerDto toDto(MessageStateResponse message, @Context PartnerLookUp lookUp);

    public List<MessageStateWithPartnerDto> toDtos(List<MessageStateResponse> messages, @Context PartnerLookUp lookUp) {
        return messages.stream().map(m -> toDto(m, lookUp)).toList();
    }

    public PartnerResponse toPartner(UUID id, @Context PartnerLookUp lookUp) {
        if (id == null) return null;
        Partner partner = lookUp.lookUp(id);
        return partner == null ? null : new PartnerResponse(partner.id(), partner.name(), partner.avatar());
    }
}
