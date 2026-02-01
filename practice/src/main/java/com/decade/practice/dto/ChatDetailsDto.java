package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatDetailsDto {
    private ChatDto chat;
    private PreferenceResponse preference;

    public static ChatDetailsDto from(Chat chat, User owner) {
        ChatDetailsDto dto = new ChatDetailsDto();
        dto.chat = new ChatDto(chat, owner);
        dto.preference = PreferenceResponse.from(chat.getPreference());
        return dto;
    }
}
