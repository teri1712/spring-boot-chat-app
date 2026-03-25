package com.decade.practice.chatorchestrator.dto;

import com.decade.practice.engagement.api.DirectMapping;

public record DirectChatResponse(DirectMapping mapping,
                                 Boolean newly) {
}