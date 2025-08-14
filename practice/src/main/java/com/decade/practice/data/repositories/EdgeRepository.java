package com.decade.practice.data.repositories;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.Edge;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.utils.EventUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EdgeRepository extends JpaRepository<Edge, UUID> {

        List<Edge> findByOwner(User owner);

        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.head = TRUE"
        )
        List<Edge> getHeadEdge(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                Pageable pageable
        );

        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.dest=:to"
        )
        List<Edge> getEdgeTo(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                @Param("to") Chat to,
                Pageable pageable
        );

        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.from=:from"
        )
        List<Edge> getEdgeFrom(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                @Param("from") Chat from,
                Pageable pageable
        );

        // Convenience methods to replace Kotlin extension functions
        default Edge getHeadEdge(User owner, int version) {
                List<Edge> edges = getHeadEdge(owner, version, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }

        default Edge getEdgeFrom(User owner, Chat from, int version) {
                List<Edge> edges = getEdgeFrom(owner, version, from, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }

        default Edge getEdgeTo(User owner, Chat to, int version) {
                List<Edge> edges = getEdgeTo(owner, version, to, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }
}