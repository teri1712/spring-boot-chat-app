package com.decade.practice.inbox.application.ports.out.projection;

import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.InboxLog;

public record LogWithConversation(InboxLog log, Conversation conversation) {

}
