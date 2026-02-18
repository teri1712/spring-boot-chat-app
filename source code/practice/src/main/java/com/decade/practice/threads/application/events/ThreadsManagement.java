package com.decade.practice.threads.application.events;

import com.decade.practice.threads.application.ports.out.DeliveryService;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.application.ports.out.UserThreadRepository;
import com.decade.practice.threads.application.ports.out.projection.EventWithHistory;
import com.decade.practice.threads.domain.ChatEvent;
import com.decade.practice.threads.domain.UserThread;
import com.decade.practice.threads.domain.events.EventCreated;
import com.decade.practice.threads.domain.events.EventReady;
import com.decade.practice.threads.domain.events.ThreadIncremented;
import com.decade.practice.threads.dto.mapper.EventMapper;
import com.decade.practice.users.api.events.IntegrationUserCreated;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
@Transactional
public class ThreadsManagement {

    private final UserThreadRepository threads;
    private final EventRepository events;
    private final EventMapper eventMapper;
    private final DeliveryService deliveryService;


    //    @KafkaListener(topics = "users.user.created", groupId = "threads-service")
    @ApplicationModuleListener
    public void on(IntegrationUserCreated userCreated) {
        UserThread userThread = new UserThread(userCreated.userId());
        threads.save(userThread);
    }


    @EventListener
    public void on(ThreadIncremented incremented) {
        ChatEvent event = events.findById(incremented.eventId()).orElseThrow();
        event.setEventVersion(incremented.eventVersion());
        events.save(event);
    }


    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void on(EventCreated eventCreated) {
        UserThread userThread = threads.findById(eventCreated.ownerId()).orElseThrow();
        userThread.increment(eventCreated.id());
        threads.save(userThread);
    }


    @TransactionalEventListener
    public void deliver(EventReady eventReady) {
        EventWithHistory event = events.findSummaryById(eventReady.id()).orElseThrow();
        deliveryService.send(eventMapper.toDto(event));
    }

}
