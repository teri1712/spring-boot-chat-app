package com.decade.practice.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ImageSpec implements Serializable {
    public static final String DEFAULT_FORMAT = "jpg";
    public static final int DEFAULT_HEIGHT = 512;
    public static final int DEFAULT_WIDTH = 512;

    @Column(updatable = false)
    private String uri;

    @Column(updatable = false)
    private String filename;

    private int width;
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
}
