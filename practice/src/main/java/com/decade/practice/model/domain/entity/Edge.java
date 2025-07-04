package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Objects;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(indexes = @Index(columnList = "event_version"))
public class Edge {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private final User owner;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "from_first"), // implicit referencedName
            @JoinColumn(name = "from_second") // implicit referencedName
    })
    private final Chat from;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "dest_first"), // implicit referencedName
            @JoinColumn(name = "dest_second") // implicit referencedName
    })
    private final Chat dest;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private final ChatEvent event;

    @Column(nullable = false)
    private final boolean head;

    @Column(nullable = false)
    private final int eventVersion;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // No-arg constructor required by JPA
    protected Edge() {
        this.owner = null;
        this.from = null;
        this.dest = null;
        this.event = null;
        this.head = false;
        this.eventVersion = 0;
    }

    public Edge(User owner, Chat from, Chat dest, ChatEvent event, boolean head) {
        this.owner = owner;
        this.from = from;
        this.dest = dest;
        this.event = event;
        this.head = head;
        this.eventVersion = event.getEventVersion();
    }

    public User getOwner() {
        return owner;
    }

    public Chat getFrom() {
        return from;
    }

    public Chat getDest() {
        return dest;
    }

    public ChatEvent getEvent() {
        return event;
    }

    public boolean isHead() {
        return head;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(id, edge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "owner=" + (owner != null ? owner.getId() : null) +
                ", from=" + (from != null ? from.getIdentifier() : null) +
                ", dest=" + (dest != null ? dest.getIdentifier() : null) +
                ", head=" + head +
                ", eventVersion=" + eventVersion +
                ", id=" + id +
                '}';
    }
}