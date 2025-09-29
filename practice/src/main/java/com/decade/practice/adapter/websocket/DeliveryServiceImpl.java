package com.decade.practice.adapter.websocket;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.ReceiptRepository;
import com.decade.practice.infra.configs.WebSocketConfiguration;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class DeliveryServiceImpl implements DeliveryService {

        public final EventStore chatEventStore;
        public final ReceiptRepository receiptRepository;
        public final SimpMessagingTemplate template;

        public DeliveryServiceImpl(EventStore chatEventStore, ReceiptRepository receiptRepository, SimpMessagingTemplate template) {
                this.chatEventStore = chatEventStore;
                this.receiptRepository = receiptRepository;
                this.template = template;
        }

        @Override
        public <E extends ChatEvent> E createAndSend(User sender, E event) {
                event.setSender(sender);
                UUID localId = event.getReceipt().getLocalId();
                try {
                        Collection<ChatEvent> savedEvents = chatEventStore.save(event);
                        for (ChatEvent savedEvent : savedEvents) {
                                template.convertAndSendToUser(
                                        savedEvent.getOwner().getUsername(),
                                        WebSocketConfiguration.QUEUE_DESTINATION,
                                        savedEvent
                                );
                        }

                        for (ChatEvent savedEvent : savedEvents) {
                                if (savedEvent.getReceipt().getLocalId().equals(localId)) {
                                        return (E) savedEvent;
                                }
                        }
                        throw new EntityNotFoundException("Event not found after save");
                } catch (DataIntegrityViolationException e) {
                        E result = (E) receiptRepository.findByLocalId(localId).getEvent();
                        return result;
                }
        }
}
