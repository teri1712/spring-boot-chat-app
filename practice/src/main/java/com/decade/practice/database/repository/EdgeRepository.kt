package com.decade.practice.database.repository

import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.Edge
import com.decade.practice.model.domain.entity.User
import com.decade.practice.utils.EventPageUtils
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface EdgeRepository : JpaRepository<Edge, UUID> {

      fun findByOwner(
            owner: User
      ): List<Edge>


      @Query(
            "SELECT e FROM Edge e " +
                    "WHERE e.owner = :owner " +
                    "AND e.eventVersion <= :eventVersion " +
                    "AND e.head = TRUE"
      )
      fun getHeadEdge(
            owner: User,
            eventVersion: Int,
            pageable: Pageable
      ): List<Edge>


      @Query(
            "SELECT e FROM Edge e " +
                    "WHERE e.owner = :owner " +
                    "AND e.eventVersion <= :eventVersion " +
                    "AND e.dest=:to"
      )
      fun getEdgeTo(
            owner: User,
            eventVersion: Int,
            to: Chat,
            pageable: Pageable
      ): List<Edge>

      @Query(
            "SELECT e FROM Edge e " +
                    "WHERE e.owner = :owner " +
                    "AND e.eventVersion <= :eventVersion " +
                    "AND e.from=:from"
      )
      fun getEdgeFrom(
            owner: User,
            eventVersion: Int,
            from: Chat,
            pageable: Pageable
      ): List<Edge>

}


fun EdgeRepository.getHeadEdge(
      owner: User,
      version: Int
): Edge? {
      return getHeadEdge(owner, version, EventPageUtils.headEvent).getOrNull(0)
}

fun EdgeRepository.getEdgeFrom(
      owner: User,
      from: Chat,
      version: Int
): Edge? {
      return getEdgeFrom(owner, version, from, EventPageUtils.headEvent).getOrNull(0)
}

fun EdgeRepository.getEdgeTo(
      owner: User,
      to: Chat,
      version: Int
): Edge? {
      return getEdgeTo(owner, version, to, EventPageUtils.headEvent).getOrNull(0)
}
