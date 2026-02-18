package com.decade.practice.threads.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record ImageSpec(
        String uri,
        String filename,
        Integer width,
        Integer height,
        String format

) implements Serializable {

}
