package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
@DiscriminatorValue(MessageUtils.TEXT)
public class TextEvent extends ChatEvent {

    @NotEmpty
    @Column(updatable = false)
    private final String content;

    // No-arg constructor required by JPA
    protected TextEvent() {
        super();
        this.content = null;
    }

    public TextEvent(Chat chat, User sender, String content) {
        super(chat, sender, MessageUtils.TEXT);
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
