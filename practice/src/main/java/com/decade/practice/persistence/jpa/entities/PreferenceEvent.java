package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.Preference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("PREFERENCE")
public class PreferenceEvent extends ChatEvent {

    @Embedded
    private Preference preference;

    public PreferenceEvent() {
    }

    public PreferenceEvent(PreferenceEvent event) {
        this(event.getChat(), event.getSender(), event.getPreference());
    }

    public PreferenceEvent(Chat chat, User sender, Preference preference) {
        super(chat, sender, "PREFERENCE");
        this.preference = preference;
    }

    @Override
    public void setChat(Chat chat) {
        super.setChat(chat);
        chat.setPreference(preference);
    }

    @Override
    public ChatEvent clone() {
        return new PreferenceEvent(this);
    }
}
