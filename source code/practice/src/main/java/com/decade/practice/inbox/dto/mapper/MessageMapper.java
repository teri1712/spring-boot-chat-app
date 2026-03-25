package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.PartnerLookUp;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.dto.*;
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
      @SubclassMapping(source = HelloGroupState.class, target = HelloGroupStateResponse.class)
      @Mapping(target = "seenBy", source = "message.seenByIds")
      @Mapping(target = "sender", source = "message.senderId")
      @Mapping(target = "postingId", source = "message.postingId")
      @Mapping(target = "sequenceNumber", source = "message.sequenceId")
      MessageStateResponse map(MessageState message, @Context PartnerLookUp partnerLookUp);


      default PartnerResponse map(UUID userId, @Context PartnerLookUp partnerLookUp) {
            Partner partner = partnerLookUp.lookUp(userId);
            return new PartnerResponse(partner.id(), partner.name(), partner.avatar());
      }

      default List<MessageStateResponse> map(List<MessageState> messages, @Context PartnerLookUp partnerLookUp) {
            return messages.stream().map(message -> map(message, partnerLookUp)).toList();
      }
}
