package com.decade.practice.threads.dto.mapper;

import com.decade.practice.threads.domain.ChatHistory;
import com.decade.practice.threads.domain.Message;
import com.decade.practice.threads.dto.ChatHistoryResponse;
import com.decade.practice.threads.dto.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatHistoryMapper {

    @Mapping(source = "chatHistoryId.chatId", target = "identifier")
    @Mapping(source = "hash.value", target = "hashValue")
    ChatHistoryResponse toHistoryResponse(ChatHistory chatHistory);

    MessageResponse toMessageResponse(Message message);
}
