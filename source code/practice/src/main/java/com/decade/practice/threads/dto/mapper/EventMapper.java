package com.decade.practice.threads.dto.mapper;

import com.decade.practice.threads.domain.*;
import com.decade.practice.threads.dto.*;
import org.hibernate.Hibernate;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING, uses = ImageMapper.class)
public interface EventMapper {

    @SubclassMapping(source = TextEvent.class, target = TextResponse.class)
    @SubclassMapping(source = IconEvent.class, target = IconResponse.class)
    @SubclassMapping(source = SeenEvent.class, target = SeenResponse.class)
    @SubclassMapping(source = ImageEvent.class, target = ImageEventResponse.class)
    @SubclassMapping(source = FileEvent.class, target = FileResponse.class)
    @SubclassMapping(source = PreferenceEvent.class, target = PreferenceEventResponse.class)
    EventResponse _toDto(ChatEvent chatEvent);

    default EventResponse toDto(ChatEvent chatEvent) {
        chatEvent = (ChatEvent) Hibernate.unproxy(chatEvent);
        return _toDto(chatEvent);
    }
}
