package com.decade.practice.events.consumers;


import com.decade.practice.application.usecases.SearchStore;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.events.UserCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SearchServiceEventListener {

    private final SearchStore searchStore;

    @KafkaListener(groupId = "decade", topics = "users")
    public void onAccountEvent(UserCreatedEvent event) {
        searchStore.save(event);
    }


    @KafkaListener(groupId = "decade", topics = "messages")
    public void onAccountEvent(MessageCreatedEvent event) {
        searchStore.save(event);
    }

}
