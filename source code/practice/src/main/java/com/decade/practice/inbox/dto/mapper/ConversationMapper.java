package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.out.UserLookUp;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.ConversationInfo;
import com.decade.practice.inbox.dto.ConversationResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {MessageMapper.class})
public interface ConversationMapper {

      @Mapping(source = "conversationId.chatId", target = "identifier")
      @Mapping(source = "hash.value", target = "revisionNumber")
      @Mapping(target = "roomName", expression = "java(infoLookUp.get(conversation.getConversationId().chatId()).name())")
      @Mapping(target = "roomAvatar", expression = "java(infoLookUp.get(conversation.getConversationId().chatId()).avatar())")
      ConversationResponse map(Conversation conversation, @Context UserLookUp userLookUp, @Context Map<String, ConversationInfo> infoLookUp);

      default List<ConversationResponse> map(List<Conversation> conversation, @Context UserLookUp userLookUp, @Context Map<String, ConversationInfo> infoLookUp) {
            return conversation.stream().map(history -> map(history, userLookUp, infoLookUp)).toList();
      }

}
