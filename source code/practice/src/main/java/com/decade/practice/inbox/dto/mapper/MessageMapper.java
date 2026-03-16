package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.dto.*;
import com.decade.practice.users.api.UserInfo;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {


      @SubclassMapping(source = TextState.class, target = TextStateResponse.class)
      @SubclassMapping(source = ImageState.class, target = ImageStateResponse.class)
      @SubclassMapping(source = IconState.class, target = IconStateResponse.class)
      @SubclassMapping(source = FileState.class, target = FileStateResponse.class)
      @SubclassMapping(source = PreferenceState.class, target = PreferenceStateResponse.class)
      @Mapping(target = "seenBy", source = "message.seenByIds")
      @Mapping(target = "sender", source = "message.senderId")
      @Mapping(target = "postingId", source = "message.postingId")
      @Mapping(target = "sequenceNumber", source = "message.sequenceId")
      MessageStateResponse map(MessageState message, @Context UserLookUp userLookUp);


      default UserInfo map(UUID userId, @Context UserLookUp userLookUp) {
            return userLookUp.lookUp(userId);
      }

      default List<MessageStateResponse> map(List<MessageState> messages, @Context UserLookUp userLookUp) {
            return messages.stream().map(message -> map(message, userLookUp)).toList();
      }
}
