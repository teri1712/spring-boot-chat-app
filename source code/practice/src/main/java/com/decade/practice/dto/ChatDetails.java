package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatDetails {
    private ChatDto chat;
    private UserResponse partner;
    private PreferenceResponse preference;

    public static ChatDetails from(Chat chat, User owner) {
        ChatDetails dto = new ChatDetails();
        dto.chat = new ChatDto(chat, owner);
        dto.partner = UserResponse.from(ChatUtils.inspectPartner(chat, owner));
        dto.preference = PreferenceResponse.from(chat.getPreference());
        return dto;
    }
}
