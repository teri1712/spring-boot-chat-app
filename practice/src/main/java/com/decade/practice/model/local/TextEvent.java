package com.decade.practice.model.local;

import java.util.Objects;

public class TextEvent {
    private final String content;

    public TextEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
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