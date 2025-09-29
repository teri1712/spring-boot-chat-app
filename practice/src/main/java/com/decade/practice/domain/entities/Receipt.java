package com.decade.practice.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Embeddable
public class Receipt {
        @Column(unique = true)
        @NotNull
        private UUID localId = UUID.randomUUID();
        private int eventVersion = SyncContext.STARTING_VERSION;

        public UUID getLocalId() {
                return localId;
        }

        public void setLocalId(UUID localId) {
                this.localId = localId;
        }

        public int getEventVersion() {
                return eventVersion;
        }

        public void setEventVersion(int version) {
                this.eventVersion = version;
        }
}
