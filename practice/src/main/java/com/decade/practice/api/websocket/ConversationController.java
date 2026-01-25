package com.decade.practice.api.websocket;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.application.events.EventSender;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.infra.configs.WebSocketConfiguration;
import com.decade.practice.infra.security.jwt.JwtUserAuthentication;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.redis.TypeEvent;
import com.decade.practice.persistence.redis.repositories.TypeRepository;
import com.decade.practice.utils.ChatUtils;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ConversationController {

    private final SimpMessagingTemplate brokerTemplate;
    private final EventSender eventSender;
    private final EventService eventService;
    private final TypeRepository typeRepository;


    @SubscribeMapping(WebSocketConfiguration.USER_QUEUE_DESTINATION)
    public EventDto subsSelf(JwtUserAuthentication user) {
        return eventService.findFirstByOwnerOrderByEventVersionDesc(user.getPrincipal().getId());
    }

    public static String resolveChatDestination(ChatIdentifier chat) {
        return WebSocketConfiguration.CHAT_DESTINATION + ":" + chat;
    }

    @MessageMapping(WebSocketConfiguration.TYPING_DESTINATION)
    @PreAuthorize("@eventStore.isAllowed(#chat,#from.principal.id)")
    public void onTyping(ChatIdentifier chat, JwtUserAuthentication from) {
        TypeEvent typeEvent = new TypeEvent();
        typeEvent.setChat(chat);
        typeEvent.setFrom(from.getPrincipal().getId());
        typeEvent.setKey(TypeEvent.determineKey(from.getPrincipal().getId(), chat));
        typeRepository.save(typeEvent);
        eventSender.send(typeEvent);
    }

    @SubscribeMapping(WebSocketConfiguration.TYPING_DESTINATION)
    @PreAuthorize("@eventStore.isAllowed(#chat,#from.principal.id)")
    public TypeEvent subsType(ChatIdentifier chat, JwtUserAuthentication from, Message<?> message) {
        // Manually for which stuff i dont remember

        brokerTemplate.send(resolveChatDestination(chat), message);
        return typeRepository.findById(TypeEvent.determineKey(ChatUtils.inspectPartner(chat, from.getPrincipal().getId()), chat)).orElse(null);
    }
}