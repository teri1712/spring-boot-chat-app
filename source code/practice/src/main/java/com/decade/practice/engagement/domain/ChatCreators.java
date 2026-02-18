package com.decade.practice.engagement.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ChatCreators(
        UUID firstCreator,
        UUID secondCreator
) implements Serializable {

}