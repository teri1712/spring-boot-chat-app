package com.decade.practice.domain.entities;

import com.decade.practice.domain.embeddables.Preference;
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
                super(chat, sender, "PREFERENCE");
                this.preference = preference;
        }

        public Preference getPreference() {
                return preference;
        }

        @Override
        public void setChat(Chat chat) {
                super.setChat(chat);
                chat.setPreference(preference);
        }

        public void setPreference(Preference preference) {
                this.preference = preference;
        }

        @Override
        protected void bindExtraProperties() {
                super.bindExtraProperties();
                extraProperties.put("preferenceEvent", new com.decade.practice.domain.locals.PreferenceEvent(preference));
        }

        @Override
        public ChatEvent copy() {
                return new PreferenceEvent(this);
        }
}
