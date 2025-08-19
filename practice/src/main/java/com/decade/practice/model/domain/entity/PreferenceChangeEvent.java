package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.embeddable.Preference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PREFERENCE_CHANGE")
public class PreferenceChangeEvent extends ChatEvent {

        @Embedded
        private Preference preference;

        public PreferenceChangeEvent() {
        }

        public PreferenceChangeEvent(PreferenceChangeEvent event) {
                this(event.getChat(), event.getSender(), event.getPreference());
        }

        public PreferenceChangeEvent(Chat chat, User sender, Preference preference) {
                super(chat, sender, "PREFERENCE_CHANGE");
                this.preference = preference;
        }

        public Preference getPreference() {
                return preference;
        }

        public void setPreference(Preference preference) {
                this.preference = preference;
        }

        @Override
        public ChatEvent copy() {
                return new PreferenceChangeEvent(this);
        }
}
