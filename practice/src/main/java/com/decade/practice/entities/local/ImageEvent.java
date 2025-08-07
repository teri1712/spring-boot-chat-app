package com.decade.practice.entities.local;

import com.decade.practice.entities.domain.embeddable.ImageSpec;

import java.util.Objects;

public class ImageEvent {
      private ImageSpec imageSpec;

      public ImageEvent(ImageSpec imageSpec) {
            this.imageSpec = imageSpec;
      }

      protected ImageEvent() {
      }

      public ImageSpec getImageSpec() {
            return imageSpec;
      }

      public void setImageSpec(ImageSpec imageSpec) {
            this.imageSpec = imageSpec;
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