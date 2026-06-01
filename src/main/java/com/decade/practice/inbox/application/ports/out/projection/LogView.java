package com.decade.practice.inbox.application.ports.out.projection;

import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.Message;

public record LogView(InboxLog log, Message message, ConversationView conversationView) {

}
