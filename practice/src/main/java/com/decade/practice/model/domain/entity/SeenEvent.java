package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("SEEN")
public class SeenEvent extends ChatEvent {

        @NotNull
        @Column(updatable = false)
        private long at;

        protected SeenEvent() {
                super();
        }

        public SeenEvent(Chat chat, User sender, long at) {
                super(chat, sender, "SEEN");
                this.at = at;
        }

        public SeenEvent(SeenEvent event) {
                this(event.getChat(), event.getSender(), event.getAt());
        }

        @Override
        public ChatEvent copy() {
                return new SeenEvent(this);
        }

        @JsonGetter
        public com.decade.practice.model.local.SeenEvent getSeenEvent() {
                return new com.decade.practice.model.local.SeenEvent(at);
        }

        public long getAt() {
                return at;
        }
}