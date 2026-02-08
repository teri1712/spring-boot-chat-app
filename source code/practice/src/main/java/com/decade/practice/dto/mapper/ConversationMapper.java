package com.decade.practice.dto.mapper;

import com.decade.practice.dto.Conversation;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, ChatMapper.class})
public interface ConversationMapper {

    Conversation toConversation(Chat chat, User partner, User owner);

    default Conversation toConversation(Chat chat, User owner) {
        return toConversation(chat, ChatUtils.inspectPartner(chat, owner), owner);
    }
}
