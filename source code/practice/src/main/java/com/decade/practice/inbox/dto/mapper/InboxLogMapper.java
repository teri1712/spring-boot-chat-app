package com.decade.practice.inbox.dto.mapper;


import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.dto.*;
import com.decade.practice.users.api.UserInfo;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PartnerMapper.class})
public interface InboxLogMapper {
      @Mapping(target = "roomNameSnapshot", expression = "java(context.roomNameSnapshot())")
      @Mapping(target = "roomAvatarSnapshot", expression = "java(context.roomAvatarSnapshot())")
      InboxLogResponse map(InboxLogCreated inboxLogCreated, @Context InboxContext context);


      @Mapping(target = "roomNameSnapshot", expression = "java(context.roomNameSnapshot())")
      @Mapping(target = "roomAvatarSnapshot", expression = "java(context.roomAvatarSnapshot())")
      InboxLogResponse map(InboxLog inboxLog, @Context InboxContext context);


      @SubclassMapping(source = TextState.class, target = TextStateResponse.class)
      @SubclassMapping(source = ImageState.class, target = ImageStateResponse.class)
      @SubclassMapping(source = IconState.class, target = IconStateResponse.class)
      @SubclassMapping(source = FileState.class, target = FileStateResponse.class)
      @SubclassMapping(source = PreferenceState.class, target = PreferenceStateResponse.class)
      @Mapping(target = "seenBy", source = "message.seenByIds", qualifiedByName = "userResolver")
      @Mapping(target = "sender", source = "message.senderId", qualifiedByName = "userResolver")
      MessageStateResponse map(MessageState message, @Context InboxContext context);


      default List<InboxLogResponse> map(List<InboxLog> inboxLogs, InboxContext context) {
            return inboxLogs.stream().map(inboxLog -> map(inboxLog, context)).toList();
      }

      @Named("userResolver")
      default UserInfo resolveUser(UUID senderId, @Context InboxContext context) {
            return context.userMap().get(senderId);
      }

      record InboxContext(Map<UUID, UserInfo> userMap, String roomNameSnapshot, String roomAvatarSnapshot) {
      }

}
