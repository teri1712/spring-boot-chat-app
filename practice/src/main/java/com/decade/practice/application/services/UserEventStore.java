package com.decade.practice.application.services;

import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.repositories.EdgeRepository;
import com.decade.practice.domain.repositories.EventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Component
public class UserEventStore implements EventStore {

        private final EdgeRepository edgeRepo;
        private final EventRepository eventRepo;

        public UserEventStore(
                EdgeRepository edgeRepo,
                EventRepository eventRepo
        ) {
                this.edgeRepo = edgeRepo;
                this.eventRepo = eventRepo;
        }

        @Transactional(
                propagation = Propagation.REQUIRED,
                isolation = Isolation.READ_COMMITTED
        )
        @Override
        public Collection<ChatEvent> save(ChatEvent event) {
                User owner = event.getOwner();
                Chat chat = event.getChat();

                SyncContext syncContext = owner.getSyncContext();
                int version = syncContext.getEventVersion() + 1;
                syncContext.setEventVersion(version);

                event.setEventVersion(version);

                if (event instanceof MessageEvent) {
                        Edge head = edgeRepo.findHeadEdge(owner, version);
                        if (head != null) {
                                Chat top = head.getFrom();
                                if (top != chat) {
                                        Edge newHead = new Edge(
                                                owner,
                                                chat,
                                                top,
                                                event,
                                                true
                                        );
                                        event.getEdges().add(newHead);

                                        Edge bridgeFrom = edgeRepo.findEdgeTo(owner, chat, version);
                                        if (bridgeFrom != null) {
                                                Chat from = bridgeFrom.getFrom();
                                                Edge bridgeTo = edgeRepo.findEdgeFrom(owner, chat, version);
                                                if (bridgeTo != null) {
                                                        Chat dest = bridgeTo.getDest();
                                                        Edge bridgeEdge = new Edge(
                                                                owner,
                                                                from,
                                                                dest,
                                                                event,
                                                                false
                                                        );
                                                        event.getEdges().add(bridgeEdge);
                                                }
                                        }
                                }
                        }
                }

                eventRepo.save(event);
                return Collections.singletonList(event);
        }
}