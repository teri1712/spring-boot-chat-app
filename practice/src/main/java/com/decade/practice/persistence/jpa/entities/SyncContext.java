package com.decade.practice.persistence.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class SyncContext {
    public static final int STARTING_VERSION = 0;

    @MapsId
    @OneToOne
    @JsonIgnore
    private User owner;

    @Id
    private UUID id;

    private int eventVersion = STARTING_VERSION;

    // No-arg constructor required by JPA
    protected SyncContext() {
    }

    public SyncContext(User owner) {
        this.owner = owner;
    }

    public void incVersion() {
        this.setEventVersion(this.getEventVersion() + 1);
    }

}