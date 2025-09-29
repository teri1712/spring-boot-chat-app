package com.decade.practice.application.services;

import com.decade.practice.application.usecases.EventService;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.ChatEvent;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.EventRepository;
import com.decade.practice.utils.EventUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

        public final EventRepository evenRepo;

        public EventServiceImpl(EventRepository evenRepo) {
                this.evenRepo = evenRepo;
        }

        @Override
        @Cacheable(cacheNames = "events", key = "#owner.id + ':' + #chat.toString() + ':' + #eventVersion")
        public List<ChatEvent> findByOwnerAndChatAndEventVersionLessThanEqual(User owner, Chat chat, int eventVersion) {
                return evenRepo.findByOwnerAndChatAndReceipt_EventVersionLessThanEqual(owner, chat, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);
        }

        @Override
        @Cacheable(cacheNames = "events", key = "#owner.id + ':' + #eventVersion")
        public List<ChatEvent> findByOwnerAndEventVersionLessThanEqual(User owner, int eventVersion) {
                return evenRepo.findByOwnerAndReceipt_EventVersionLessThanEqual(owner, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);
        }

        @Override
        public ChatEvent findFirstByOwnerOrderByEventVersionDesc(User owner) {
                return evenRepo.findFirstByOwnerOrderByReceipt_EventVersionDesc(owner);
        }

}
