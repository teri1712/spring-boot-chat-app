package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.embeddable.Preference;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

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
                super(chat, sender, "PREFERENCE_CHANGE");
                this.preference = preference;
        }

        public Preference getPreference() {
                return preference;
        }

        public void setPreference(Preference preference) {
                this.preference = preference;
        }

        @JsonGetter
        public com.decade.practice.model.local.PreferenceEvent getPreferenceEvent() {
                return new com.decade.practice.model.local.PreferenceEvent(preference);
        }

        @Override
        public ChatEvent copy() {
                return new PreferenceEvent(this);
        }
}
