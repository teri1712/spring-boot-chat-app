package com.decade.practice.model.local;

import com.decade.practice.model.domain.embeddable.Preference;

public class PreferenceEvent {
        private Preference preference;

        public PreferenceEvent(Preference preference) {
                this.preference = preference;
        }

        public Preference getPreference() {
                return preference;
        }

        protected PreferenceEvent() {
        }

        public void setPreference(Preference preference) {
                this.preference = preference;
        }
}
