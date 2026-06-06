package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.dto.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {


    @SubclassMapping(source = TextState.class, target = TextStateResponse.class)
    @SubclassMapping(source = ImageState.class, target = ImageStateResponse.class)
    @SubclassMapping(source = IconState.class, target = IconStateResponse.class)
    @SubclassMapping(source = FileState.class, target = FileStateResponse.class)
    @SubclassMapping(source = PreferenceState.class, target = PreferenceStateResponse.class)
    @SubclassMapping(source = HelloGroupState.class, target = HelloGroupStateResponse.class)
    @Mapping(target = "sequenceNumber", source = "message.sequenceId")
    MessageStateResponse map(MessageState message);


}
