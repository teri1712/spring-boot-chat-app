package com.decade.practice.domain.embeddables;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Embeddable
public class ImageSpec implements Serializable {
        public static final String DEFAULT_FORMAT = "jpg";
        public static final int DEFAULT_HEIGHT = 512;
        public static final int DEFAULT_WIDTH = 512;

        private final String uri;
        private final String filename;
        @NotNull
        private int width;
        @NotNull
        private int height;
        private String format;

        // No-arg constructor required by JPA
        public ImageSpec() {
                this.uri = "";
                this.filename = "";
                this.width = DEFAULT_WIDTH;
                this.height = DEFAULT_HEIGHT;
                this.format = DEFAULT_FORMAT;
        }

        public ImageSpec(String uri, String filename, int width, int height, String format) {
                this.uri = uri;
                this.filename = filename;
                this.width = width;
                this.height = height;
                this.format = format;
        }

        public String getUri() {
                return uri;
        }

        public String getFilename() {
                return filename;
        }

        public int getWidth() {
                return width;
        }

        public void setWidth(int width) {
                this.width = width;
        }

        public int getHeight() {
                return height;
        }

        public void setHeight(int height) {
                this.height = height;
        }

        public String getFormat() {
                return format;
        }

        public void setFormat(String format) {
                this.format = format;
        }
}
