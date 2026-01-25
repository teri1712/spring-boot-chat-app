package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TEXT")
@Setter
@Getter
@NoArgsConstructor
public class TextEvent extends MessageEvent {

    @Column(updatable = false)
    private String content;

    public TextEvent(Chat chat, User sender, String content) {
        super(chat, sender, "TEXT");
        this.content = content;
    }

    public TextEvent(TextEvent event) {
        this(event.getChat(), event.getSender(), event.getContent());
    }

    @Override
    public ChatEvent clone() {
        return new TextEvent(this);
    }


}
