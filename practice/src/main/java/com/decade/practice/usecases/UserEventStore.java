package com.decade.practice.usecases;

import com.decade.practice.core.EventStore;
import com.decade.practice.database.repository.EdgeRepository;
import com.decade.practice.database.repository.EventRepository;
import com.decade.practice.model.domain.SyncContext;
import com.decade.practice.model.domain.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserEventStore implements EventStore {

      private final EdgeRepository edgeRepo;
      private final EventRepository eventRepo;

      @PersistenceContext
      private EntityManager em;

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

            if (MessageUtils.isMessage(event)) {
                  Edge head = edgeRepo.getHeadEdge(owner, version);
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

                              Edge bridgeFrom = edgeRepo.getEdgeTo(owner, chat, version);
                              if (bridgeFrom != null) {
                                    Chat from = bridgeFrom.getFrom();
                                    Edge bridgeTo = edgeRepo.getEdgeFrom(owner, chat, version);
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