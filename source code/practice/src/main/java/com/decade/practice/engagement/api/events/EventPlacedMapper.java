package com.decade.practice.engagement.api.events;

import com.decade.practice.engagement.domain.Preference;
import com.decade.practice.engagement.domain.events.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventPlacedMapper {

    @SubclassMapping(target = TextEventPlaced.class, source = TextParticipantPlaced.class)
    @SubclassMapping(target = IconEventPlaced.class, source = IconParticipantPlaced.class)
    @SubclassMapping(target = SeenEventPlaced.class, source = SeenParticipantPlaced.class)
    @SubclassMapping(target = ImageEventPlaced.class, source = ImageParticipantPlaced.class)
    @SubclassMapping(target = FileEventPlaced.class, source = FileParticipantPlaced.class)
    @SubclassMapping(target = PreferenceEventPlaced.class, source = PreferenceChangePlaced.class)
    @Mapping(target = "snapshot", expression = "java(snapshot)")
    EventPlaced toIntegration(ParticipantPlaced eventPlaced, @Context ChatSnapshot snapshot);


    @Mapping(target = "iconId", source = "preference.iconId")
    @Mapping(target = "roomName", source = "preference.roomName")
    @Mapping(target = "roomAvatar", source = "preference.roomAvatar")
    @Mapping(target = "theme", source = "preference.theme")
    PreferenceChangePlaced toPrefPlaced(PreferenceParticipantPlaced placed, Preference preference);
}
