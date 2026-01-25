package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ICON")
@Setter
@Getter
@NoArgsConstructor
public class IconEvent extends MessageEvent {

    @NotNull
    @Column(updatable = false)
    private int resourceId;


    public IconEvent(Chat chat, User sender, int resourceId) {
        super(chat, sender, "ICON");
        this.resourceId = resourceId;
    }

    public IconEvent(IconEvent event) {
        this(event.getChat(), event.getSender(), event.getResourceId());
    }


    @Override
    public ChatEvent clone() {
        return new IconEvent(this);
    }
}