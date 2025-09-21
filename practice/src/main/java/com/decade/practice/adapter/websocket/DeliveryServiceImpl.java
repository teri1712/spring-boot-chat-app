package com.decade.practice.adapter.websocket;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.EventRepository;
import com.decade.practice.infra.configs.WebSocketConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DeliveryServiceImpl implements DeliveryService {

        public final EventStore chatEventStore;
        public final EventRepository evenRepo;
        public final SimpMessagingTemplate template;

        public DeliveryServiceImpl(EventStore chatEventStore, EventRepository evenRepo, SimpMessagingTemplate template) {
                this.chatEventStore = chatEventStore;
                this.evenRepo = evenRepo;
                this.template = template;
        }

        @Override
        public <E extends ChatEvent> E createAndSend(User sender, E event) {
                event.setSender(sender);
                try {
                        Collection<ChatEvent> saved = chatEventStore.save(event);
                        for (ChatEvent it : saved) {
                                template.convertAndSendToUser(
                                        it.getOwner().getUsername(),
                                        WebSocketConfiguration.QUEUE_DESTINATION,
                                        it
                                );
                        }

                        // Find the event with the same localId as the original event
                        for (ChatEvent it : saved) {
                                if (it.getLocalId().equals(event.getLocalId())) {
                                        @SuppressWarnings("unchecked")
                                        E result = (E) it;
                                        return result;
                                }
                        }
                        throw new EntityNotFoundException("Event not found after save");
                } catch (DataIntegrityViolationException e) {
                        // record already sent
                        @SuppressWarnings("unchecked")
                        E result = (E) evenRepo.findByLocalId(event.getLocalId());
                        return result;
                }
        }
}
