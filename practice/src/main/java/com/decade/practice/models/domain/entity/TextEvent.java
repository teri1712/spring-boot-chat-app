package com.decade.practice.models.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotEmpty;

@Entity
@DiscriminatorValue("TEXT")
public class TextEvent extends MessageEvent {

        @NotEmpty
        @Lob
        @Column(updatable = false)
        private String content;

        protected TextEvent() {
                super();
        }

        public TextEvent(Chat chat, User sender, String content) {
                super(chat, sender, "TEXT");
                this.content = content;
        }

        public TextEvent(TextEvent event) {
                this(event.getChat(), event.getSender(), event.getContent());
        }

        @Override
        public ChatEvent copy() {
                return new TextEvent(this);
        }

        @Override
        protected void bindExtraProperties() {
                super.bindExtraProperties();
                extraProperties.put("textEvent", new com.decade.practice.models.local.TextEvent(content));
        }


        public String getContent() {
                return content;
        }
}
