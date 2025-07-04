package com.decade.practice.model.local;

import com.decade.practice.model.domain.embeddable.ImageSpec;

import java.util.Objects;

public class ImageEvent {
    private final ImageSpec imageSpec;

    public ImageEvent(ImageSpec imageSpec) {
        this.imageSpec = imageSpec;
    }

    public ImageSpec getImageSpec() {
        return imageSpec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageEvent that = (ImageEvent) o;
        return Objects.equals(imageSpec, that.imageSpec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageSpec);
    }

    @Override
    public String toString() {
        return "ImageEvent{" +
                "imageSpec=" + imageSpec +
                '}';
    }
}