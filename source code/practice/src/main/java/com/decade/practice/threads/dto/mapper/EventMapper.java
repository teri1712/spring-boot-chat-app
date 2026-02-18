package com.decade.practice.threads.dto.mapper;

import com.decade.practice.threads.application.ports.out.projection.EventWithHistory;
import com.decade.practice.threads.domain.*;
import com.decade.practice.threads.dto.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING, uses = ImageMapper.class)
public interface EventMapper {

    @SubclassMapping(source = TextEvent.class, target = TextResponse.class)
    @SubclassMapping(source = IconEvent.class, target = IconResponse.class)
    @SubclassMapping(source = SeenEvent.class, target = SeenResponse.class)
    @SubclassMapping(source = ImageEvent.class, target = ImageEventResponse.class)
    @SubclassMapping(source = FileEvent.class, target = FileResponse.class)
    @SubclassMapping(source = PreferenceEvent.class, target = PreferenceEventResponse.class)
    @Mapping(target = "roomNameSnapshot", expression = "java(resolver.roomNameSnapshot())")
    @Mapping(target = "roomAvatarSnapshot", expression = "java(resolver.roomAvatarSnapshot())")
    @Mapping(target = "roomHashSnapshot", expression = "java(resolver.roomHashSnapshot())")
    EventResponse _toDto(ChatEvent chatEvent, @Context RoomSnapshotResolver resolver);


    default EventResponse toDto(EventWithHistory eventWithHistory) {
        ChatHistory history = eventWithHistory.history();
        return _toDto(eventWithHistory.event(), new RoomSnapshotResolver(history.getRoomName(), history.getRoomAvatar(), history.getHash().value()));
    }

    record RoomSnapshotResolver(String roomNameSnapshot,
                                String roomAvatarSnapshot,
                                Long roomHashSnapshot) {
    }
}
