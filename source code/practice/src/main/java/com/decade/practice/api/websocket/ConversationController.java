package com.decade.practice.api.websocket;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.TypeEventDto;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.application.usecases.LiveService;
import com.decade.practice.infra.configs.WebSocketConfiguration;
import com.decade.practice.infra.security.jwt.JwtUserAuthentication;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.redis.TypeEvent;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ConversationController {

    private final LiveService liveService;
    private final EventService eventService;


    @SubscribeMapping(WebSocketConfiguration.USER_QUEUE_DESTINATION)
    public EventDto subsSelf(JwtUserAuthentication user) {
        return eventService.findFirstByOwnerOrderByEventVersionDesc(user.getPrincipal().getId());
    }

    public static String resolveChatDestination(ChatIdentifier chat) {
        return WebSocketConfiguration.CHAT_DESTINATION + ":" + chat;
    }

    @MessageMapping(WebSocketConfiguration.TYPING_DESTINATION)
    public void onTyping(ChatIdentifier chat, JwtUserAuthentication from) {
        TypeEventDto typeEvent = new TypeEventDto();
        typeEvent.setChat(chat);
        typeEvent.setFrom(from.getPrincipal().getId());
        typeEvent.setKey(TypeEvent.determineKey(from.getPrincipal().getId(), chat));
        liveService.send(chat, typeEvent);
    }

    @SubscribeMapping(WebSocketConfiguration.TYPING_DESTINATION)
    public void subsType(ChatIdentifier chat, JwtUserAuthentication from, Message<?> message) {
        liveService.subscribe(chat, from.getPrincipal().getId(), message);
    }
}