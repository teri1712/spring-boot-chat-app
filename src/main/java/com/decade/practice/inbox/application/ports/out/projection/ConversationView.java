package com.decade.practice.inbox.application.ports.out.projection;

import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.Room;

public record ConversationView(Conversation conversation, Room room) {
}
