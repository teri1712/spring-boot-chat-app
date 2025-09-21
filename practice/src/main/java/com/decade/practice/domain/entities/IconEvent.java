package com.decade.practice.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("ICON")
public class IconEvent extends MessageEvent {

        @NotNull
        @Column(updatable = false)
        private int resourceId;

        protected IconEvent() {
                super();
        }

        public IconEvent(Chat chat, User sender, int resourceId) {
                super(chat, sender, "ICON");
                this.resourceId = resourceId;
        }

        public IconEvent(IconEvent event) {
                this(event.getChat(), event.getSender(), event.getResourceId());
        }

        @Override
        protected void bindExtraProperties() {
                super.bindExtraProperties();
                extraProperties.put("iconEvent", new com.decade.practice.domain.locals.IconEvent(resourceId));
        }

        @Override
        public ChatEvent copy() {
                return new IconEvent(this);
        }

        public int getResourceId() {
                return resourceId;
        }
}