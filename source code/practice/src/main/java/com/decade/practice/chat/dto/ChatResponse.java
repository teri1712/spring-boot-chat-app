package com.decade.practice.chat.dto;

import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.engagement.api.ChatPolicyInfo;

public record ChatResponse(ChatPolicyInfo policy, SettingsInfo settings) {
}
