package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
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

        @JsonGetter
        public com.decade.practice.model.local.TextEvent getTextEvent() {
                return new com.decade.practice.model.local.TextEvent(content);
        }

        public String getContent() {
                return content;
        }
}
