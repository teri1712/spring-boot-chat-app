package com.decade.practice.database.transaction

import com.decade.practice.core.EventStore
import com.decade.practice.database.repository.*
import com.decade.practice.model.entity.ChatEvent
import com.decade.practice.model.entity.Edge
import com.decade.practice.model.entity.isMessage
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UserEventStore(
      private val edgeRepo: EdgeRepository,
      private val eventRepo: EventRepository
) : EventStore {

      @PersistenceContext
      private lateinit var em: EntityManager


      @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED
      )
      override fun save(event: ChatEvent): Collection<ChatEvent> {

            val owner = event.owner
            val chat = event.chat

            val version = owner.syncContext.incVersion()
            event.eventVersion = version

            if (event.isMessage()) {
                  val head = edgeRepo.getHeadEdge(owner, version)!!
                  val top = head.from
                  if (top !== chat) {
                        val headEdge = Edge(
                              owner = owner,
                              from = chat,
                              dest = top,
                              event = event,
                              head = true
                        )
                        event.edges.add(headEdge)
                        val bridgeFrom = edgeRepo.getEdgeTo(owner, chat, version)?.from

                        if (bridgeFrom != null) {
                              val bridgeTo = edgeRepo.getEdgeFrom(owner, chat, version)?.dest
                              val bridgeEdge = Edge(
                                    owner = owner,
                                    from = bridgeFrom,
                                    dest = bridgeTo,
                                    event = event,
                                    head = false
                              )
                              event.edges.add(bridgeEdge)
                        }
                  }
            }
            eventRepo.save(event)
            return listOf(event)
      }
}
