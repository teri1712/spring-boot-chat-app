package com.decade.practice.application.services;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventConverterResolution;
import com.decade.practice.application.usecases.EventSender;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {


    private final ChatEventStore eventStore;
    private final EventSender eventSender;
    private final EventRepository eventRepo;
    private final EventConverterResolution converterResolution;

    @Override
    public EventDto createAndSend(UUID senderId, ChatIdentifier chatIdentifier, UUID idempotentKey, EventRequest eventRequest) {

        try {

            List<EventDto> stored = eventStore.save(senderId, senderId, idempotentKey, chatIdentifier, eventRequest);
            stored.forEach(eventSender::send);

            for (int i = 0; i < stored.size(); i++) {
                if (stored.get(i).getIdempotencyKey().equals(idempotentKey)) {
                    return stored.get(i);
                }
            }
        } catch (DataIntegrityViolationException e) {
            log.debug("Event already sent", e);
        }
        ChatEvent existingOne = eventRepo.findByIdempotentKey(idempotentKey).orElseThrow();
        return converterResolution.convert(existingOne);
    }
}
