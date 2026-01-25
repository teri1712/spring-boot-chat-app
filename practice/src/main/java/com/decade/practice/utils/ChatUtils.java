package com.decade.practice.utils;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;

import java.util.UUID;

public class ChatUtils {

    public static User inspectOwner(Chat chat, UUID me) {
        return chat.getIdentifier().getFirstUser().equals(me) ? chat.getFirstUser() : chat.getSecondUser();
    }

    public static User inspectOwner(Chat chat, User me) {
        return chat.getFirstUser().equals(me) ? chat.getFirstUser() : chat.getSecondUser();
    }

    public static User inspectOwner(Chat chat, String username) {
        return chat.getFirstUser().getUsername().equals(username) ? chat.getFirstUser() : chat.getSecondUser();
    }

    public static User inspectPartner(Chat chat, User me) {
        return chat.getFirstUser().equals(me) ? chat.getSecondUser() : chat.getFirstUser();
    }

    public static UUID inspectPartner(ChatIdentifier chat, UUID me) {
        return chat.getFirstUser().equals(me) ? chat.getSecondUser() : chat.getFirstUser();
    }

    public static User inspectPartner(Chat chat, UUID me) {
        return chat.getIdentifier().getFirstUser().equals(me) ? chat.getSecondUser() : chat.getFirstUser();
    }
}