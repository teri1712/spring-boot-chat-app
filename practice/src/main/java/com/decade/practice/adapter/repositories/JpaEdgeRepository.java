package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.Edge;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.EdgeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaEdgeRepository extends EdgeRepository, JpaRepository<Edge, UUID> {

        @Override
        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.head = TRUE"
        )
        List<Edge> findHeadEdge(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                Pageable pageable
        );

        @Override
        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.dest=:to"
        )
        List<Edge> findEdgeTo(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                @Param("to") Chat to,
                Pageable pageable
        );

        @Override
        @Query(
                "SELECT e FROM Edge e " +
                        "WHERE e.owner = :owner " +
                        "AND e.eventVersion <= :eventVersion " +
                        "AND e.from=:from"
        )
        List<Edge> findEdgeFrom(
                @Param("owner") User owner,
                @Param("eventVersion") int eventVersion,
                @Param("from") Chat from,
                Pageable pageable
        );

}