package com.decade.practice.model.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import java.util.Objects;
import java.util.UUID;

@Entity
public class SyncContext {
        public static final int STARTING_VERSION = 0;

        @MapsId
        @OneToOne
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

        public User getOwner() {
                return owner;
        }

        public void setOwner(User owner) {
                this.owner = owner;
        }

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public int getEventVersion() {
                return eventVersion;
        }

        public void setEventVersion(int eventVersion) {
                this.eventVersion = eventVersion;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SyncContext that = (SyncContext) o;
                return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id);
        }
}