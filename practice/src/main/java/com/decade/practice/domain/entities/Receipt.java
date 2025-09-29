package com.decade.practice.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class Receipt {
        @Id
        @GeneratedValue
        private Long id;

        @Column(unique = true)
        @NotNull
        private UUID localId = UUID.randomUUID();

        @OneToOne(mappedBy = "receipt")
        @JsonIgnore
        private ChatEvent event;

        private boolean processed = true;

        public UUID getLocalId() {
                return localId;
        }

        public void setLocalId(UUID localId) {
                this.localId = localId;
        }

        public Long getId() {
                return id;
        }

        public ChatEvent getEvent() {
                return event;
        }

        public void setEvent(ChatEvent event) {
                this.event = event;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public boolean isProcessed() {
                return processed;
        }

        public void setProcessed(boolean processed) {
                this.processed = processed;
        }
}
