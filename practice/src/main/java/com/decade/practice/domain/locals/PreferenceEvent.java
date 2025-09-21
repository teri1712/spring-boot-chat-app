package com.decade.practice.domain.locals;

import com.decade.practice.domain.embeddables.Preference;

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
