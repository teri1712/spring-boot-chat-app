package com.decade.practice.engagement.api.events;

import com.decade.practice.engagement.domain.events.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventPlacedMapper {

    @SubclassMapping(target = TextEventPlaced.class, source = TextParticipantPlaced.class)
    @SubclassMapping(target = IconEventPlaced.class, source = IconParticipantPlaced.class)
    @SubclassMapping(target = SeenEventPlaced.class, source = SeenParticipantPlaced.class)
    @SubclassMapping(target = ImageEventPlaced.class, source = ImageParticipantPlaced.class)
    @SubclassMapping(target = FileEventPlaced.class, source = FileParticipantPlaced.class)
    @SubclassMapping(target = PreferenceEventPlaced.class, source = ProcessedPreferenceParticipantPlaced.class)
    @Mapping(target = "snapshot", expression = "java(snapshot)")
    EventPlaced toIntegration(ParticipantPlaced eventPlaced, @Context ChatSnapshot snapshot);


    ProcessedPreferenceParticipantPlaced toPref(PreferenceParticipantPlaced eventPlaced, String theme);
}
