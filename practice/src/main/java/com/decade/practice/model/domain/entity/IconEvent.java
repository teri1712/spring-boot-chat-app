package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(MessageUtils.ICON)
public class IconEvent extends ChatEvent {

      @NotNull
      @Column(updatable = false)
      private int resourceId;

      // No-arg constructor required by JPA
      protected IconEvent() {
            super();
            this.resourceId = 0;
      }

      public IconEvent(Chat chat, User sender, int resourceId) {
            super(chat, sender, MessageUtils.ICON);
            this.resourceId = resourceId;
      }

      public IconEvent(IconEvent event) {
            this(event.getChat(), event.getSender(), event.getResourceId());
      }

      @Override
      public ChatEvent copy() {
            return new IconEvent(this);
      }

      @JsonGetter
      public com.decade.practice.model.local.IconEvent getIconEvent() {
            return new com.decade.practice.model.local.IconEvent(resourceId);
      }

      public int getResourceId() {
            return resourceId;
      }
}