package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.dto.*;
import org.mapstruct.*;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ImageMapper.class}
)
public interface MessageStateResponseMapper {

    @SubclassMapping(source = TextState.class, target = TextStateResponse.class)
    @SubclassMapping(source = IconState.class, target = IconStateResponse.class)
    @SubclassMapping(source = ImageState.class, target = ImageStateResponse.class)
    @SubclassMapping(source = FileState.class, target = FileStateResponse.class)
    @SubclassMapping(source = PreferenceState.class, target = PreferenceStateResponse.class)
    @SubclassMapping(source = HelloGroupState.class, target = HelloGroupStateResponse.class)
    @Mapping(target = "sequenceNumber", source = "sequenceId")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "seenByIds", source = "seenByIds")
    MessageStateResponse toResponse(MessageState message);
}
