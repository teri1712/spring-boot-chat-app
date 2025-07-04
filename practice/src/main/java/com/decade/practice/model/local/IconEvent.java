package com.decade.practice.model.local;

import java.util.Objects;

public class IconEvent {
    private final int resourceId;

    public IconEvent(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IconEvent iconEvent = (IconEvent) o;
        return resourceId == iconEvent.resourceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }

    @Override
    public String toString() {
        return "IconEvent{" +
                "resourceId=" + resourceId +
                '}';
    }
}