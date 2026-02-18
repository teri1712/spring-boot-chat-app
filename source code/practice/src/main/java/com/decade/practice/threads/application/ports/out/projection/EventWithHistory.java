package com.decade.practice.threads.application.ports.out.projection;

import com.decade.practice.threads.domain.ChatEvent;
import com.decade.practice.threads.domain.ChatHistory;

public record EventWithHistory(ChatEvent event, ChatHistory history) {

}
