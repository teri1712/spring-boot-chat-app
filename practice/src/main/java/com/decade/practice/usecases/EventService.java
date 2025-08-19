package com.decade.practice.usecases;

import com.decade.practice.data.repositories.EventRepository;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.websocket.WebSocketConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EventService implements EventOperations {
        public final EventStore eventStore;
        public final EventRepository evenRepo;
        public final SimpMessagingTemplate template;

        public EventService(EventStore eventStore, EventRepository evenRepo, SimpMessagingTemplate template) {
                this.eventStore = eventStore;
                this.evenRepo = evenRepo;
                this.template = template;
        }


        @Override
        public <E extends ChatEvent> E createAndSend(User sender, E event) {
                event.setSender(sender);
                try {
                        Collection<ChatEvent> saved = eventStore.save(event);
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
                        E result = (E) evenRepo.getByLocalId(event.getLocalId());
                        return result;
                }
        }
}
