package com.decade.practice.domain.entities;

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

        @Override
        protected void bindExtraProperties() {
                super.bindExtraProperties();
                extraProperties.put("seenEvent", new com.decade.practice.domain.locals.SeenEvent(at));
        }


        public long getAt() {
                return at;
        }
}