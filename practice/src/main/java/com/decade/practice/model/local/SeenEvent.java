package com.decade.practice.model.local;

import java.util.Objects;

public class SeenEvent {
        private long at;

        public SeenEvent(long at) {
                this.at = at;
        }

        protected SeenEvent() {
        }

        public long getAt() {
                return at;
        }

        public void setAt(long at) {
                this.at = at;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SeenEvent seenEvent = (SeenEvent) o;
                return at == seenEvent.at;
        }

        @Override
        public int hashCode() {
                return Objects.hash(at);
        }

        @Override
        public String toString() {
                return "SeenEvent{" +
                        "at=" + at +
                        '}';
        }
}