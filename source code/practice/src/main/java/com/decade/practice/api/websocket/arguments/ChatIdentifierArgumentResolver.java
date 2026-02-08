package com.decade.practice.api.websocket.arguments;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

import java.util.List;
import java.util.Map;

public class ChatIdentifierArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String CHAT_HEADER = "chat_identifier";

    public static ChatCreators resolveChatHeader(Message<?> message) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> nativeHeaders =
                (Map<String, List<String>>) message.getHeaders().get(NativeMessageHeaderAccessor.NATIVE_HEADERS);

        List<String> chatHeaderValues = nativeHeaders.get(CHAT_HEADER);
        if (chatHeaderValues == null || chatHeaderValues.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty '" + CHAT_HEADER + "' header");
        }

        String chatHeaderValue = chatHeaderValues.get(0);
        return ChatIdentifierConverter.extractChatIdentifier(chatHeaderValue);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == ChatCreators.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) {
        return resolveChatHeader(message);
    }
}