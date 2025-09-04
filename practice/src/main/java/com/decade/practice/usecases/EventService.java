package com.decade.practice.usecases;

import com.decade.practice.data.repositories.EventRepository;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.ChatEvent;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.websocket.WebSocketConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
                        E result = (E) evenRepo.findByLocalId(event.getLocalId());
                        return result;
                }
        }

        @Override
        @Cacheable(cacheNames = "events", key = "#owner.id + ':' + #chat.toString() + ':' + #eventVersion")
        public List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(User owner, Chat chat, int eventVersion, Pageable pageable) {
                return evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, eventVersion, pageable);
        }

        @Override
        @Cacheable(cacheNames = "events", key = "#owner.id + ':' + #eventVersion")
        public List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(User owner, int eventVersion, Pageable pageable) {
                return evenRepo.findByOwnerAndEventVersionLessThanEqual(owner, eventVersion, pageable);
        }

        @Override
        public ChatEvent findFirstByOwnerOrderByEventVersionDesc(User owner) {
                return evenRepo.findFirstByOwnerOrderByEventVersionDesc(owner);
        }

        @Override
        public ChatEvent findByLocalId(UUID localId) {
                return evenRepo.findByLocalId(localId);
        }
}
