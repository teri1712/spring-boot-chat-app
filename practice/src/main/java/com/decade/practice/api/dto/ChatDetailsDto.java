package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
