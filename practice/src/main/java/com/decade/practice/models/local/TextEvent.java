package com.decade.practice.models.local;

import java.util.Objects;

public class TextEvent {
        private String content;

        public TextEvent(String content) {
                this.content = content;
        }

        protected TextEvent() {
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TextEvent textEvent = (TextEvent) o;
                return Objects.equals(content, textEvent.content);
        }

        @Override
        public int hashCode() {
                return Objects.hash(content);
        }

        @Override
        public String toString() {
                return "TextEvent{" +
                        "content='" + content + '\'' +
                        '}';
        }
}