package com.decade.practice.domain.repositories;

import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.Edge;
import com.decade.practice.domain.entities.User;
import com.decade.practice.utils.EventUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean

public interface EdgeRepository extends CrudRepository<Edge, UUID> {

        List<Edge> findByOwner(User owner);

        List<Edge> findHeadEdge(User owner, int eventVersion, Pageable pageable);

        List<Edge> findEdgeTo(User owner, int eventVersion, Chat to, Pageable pageable);

        List<Edge> findEdgeFrom(User owner, int eventVersion, Chat from, Pageable pageable);

        default Edge findHeadEdge(User owner, int version) {
                List<Edge> edges = findHeadEdge(owner, version, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }

        default Edge findEdgeFrom(User owner, Chat from, int version) {
                List<Edge> edges = findEdgeFrom(owner, version, from, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }

        default Edge findEdgeTo(User owner, Chat to, int version) {
                List<Edge> edges = findEdgeTo(owner, version, to, EventUtils.headEvent);
                return edges.isEmpty() ? null : edges.get(0);
        }
}