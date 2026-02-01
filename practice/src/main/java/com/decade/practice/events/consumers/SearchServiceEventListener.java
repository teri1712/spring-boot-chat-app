package com.decade.practice.events.consumers;


import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.UserCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SearchServiceEventListener {

    private final SearchService searchService;

    @KafkaListener(groupId = "decade", topics = "accounts")
    public void onAccountEvent(UserCreatedEvent event) {
    }


    @KafkaListener(groupId = "decade", topics = "messages")
    public void onAccountEvent(EventDto event) {
    }

}
