package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.dto.ConversationResponse;
import com.decade.practice.users.api.UserInfo;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = PartnerMapper.class)
public interface ChatHistoryMapper {

      @Mapping(source = "conversationId.chatId", target = "identifier")
      @Mapping(source = "hash.value", target = "hashValue")
      ConversationResponse map(Conversation conversation, @Context Map<UUID, UserInfo> userMap);

      default List<ConversationResponse> map(List<Conversation> conversation, @Context Map<UUID, UserInfo> userMap) {
            return conversation.stream().map(history -> map(history, userMap)).toList();
      }

}
