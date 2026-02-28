package com.decade.practice.engagement.dto.events;

import com.decade.practice.engagement.domain.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventPlacedMapper {

      @SubclassMapping(target = TextIntegrationChatEventPlaced.class, source = TextChatEvent.class)
      @SubclassMapping(target = IconIntegrationChatEventPlaced.class, source = IconChatEvent.class)
      @SubclassMapping(target = SeenIntegrationChatEventPlaced.class, source = SeenChatEvent.class)
      @SubclassMapping(target = ImageIntegrationChatEventPlaced.class, source = ImageChatEvent.class)
      @SubclassMapping(target = FileIntegrationChatEventPlaced.class, source = FileChatEvent.class)
      @SubclassMapping(target = PreferenceIntegrationChatEventPlaced.class, source = PreferenceChatEvent.class)
      @Mapping(target = "snapshot", expression = "java(snapshot)")
      @Mapping(target = "chatEventId", source = "id")
      IntegrationChatEventPlaced map(ChatEvent chatEvent, @Context IntegrationChatSnapshot snapshot);

}
