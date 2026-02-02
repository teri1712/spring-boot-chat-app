package com.decade.practice.events.consumers;


import com.decade.practice.application.usecases.SearchStore;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.events.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SearchServiceEventListener {

    private final SearchStore searchStore;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "users")
    public void onAccountEvent(String eventString) throws JsonProcessingException {
        UserCreatedEvent event = objectMapper.readValue(eventString, UserCreatedEvent.class);
        log.trace("Received message: {}", event);
        searchStore.save(event);
    }


    @KafkaListener(topics = "messages")
    public void onMessageEvent(String eventString) throws JsonProcessingException {
        MessageCreatedEvent event = objectMapper.readValue(eventString, MessageCreatedEvent.class);
        log.trace("Received message: {}", event);
        searchStore.save(event);
    }

}
