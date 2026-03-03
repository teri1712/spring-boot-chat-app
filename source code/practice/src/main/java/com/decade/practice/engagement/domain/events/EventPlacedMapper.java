package com.decade.practice.engagement.domain.events;

import com.decade.practice.engagement.domain.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventPlacedMapper {

      @SubclassMapping(target = TextChatEventAccepted.class, source = TextChatEvent.class)
      @SubclassMapping(target = IconChatEventAccepted.class, source = IconChatEvent.class)
      @SubclassMapping(target = SeenChatEventAccepted.class, source = SeenChatEvent.class)
      @SubclassMapping(target = ImageChatEventAccepted.class, source = ImageChatEvent.class)
      @SubclassMapping(target = FileChatEventAccepted.class, source = FileChatEvent.class)
      @SubclassMapping(target = PreferenceChatEventAccepted.class, source = PreferenceChatEvent.class)
      @Mapping(target = "snapshot", expression = "java(snapshot)")
      @Mapping(target = "chatEventId", source = "id")
      ChatEventAccepted map(ChatEvent chatEvent, @Context ChatSnapshot snapshot);

}
