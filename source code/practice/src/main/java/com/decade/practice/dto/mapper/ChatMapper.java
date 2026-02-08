package com.decade.practice.dto.mapper;

import com.decade.practice.dto.ChatDetails;
import com.decade.practice.dto.ChatResponse;
import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, PreferenceMapper.class})
public interface ChatMapper {

    ChatResponse toChatResponse(String identifier, UUID owner, UUID partner);

    @Mapping(source = "chat.preference", target = "preference")
    ChatDetails toChatDetails(Chat chat, User partner);

    default ChatResponse toChatResponse(ChatCreators chat, UUID owner) {
        return toChatResponse(chat, owner, ChatUtils.inspectPartner(chat, owner));
    }


}
