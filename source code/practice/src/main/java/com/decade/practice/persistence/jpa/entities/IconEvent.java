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
    private int iconId;


    public IconEvent(Chat chat, User sender, int iconId) {
        super(chat, sender, "ICON");
        this.iconId = iconId;
    }

    public IconEvent(IconEvent event) {
        this(event.getChat(), event.getSender(), event.getIconId());
    }


    @Override
    public ChatEvent clone() {
        return new IconEvent(this);
    }
}