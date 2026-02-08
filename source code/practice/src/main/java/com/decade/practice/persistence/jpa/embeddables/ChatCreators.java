package com.decade.practice.persistence.jpa.embeddables;

import com.decade.practice.persistence.jpa.entities.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Embeddable
// TODO: Adjust flyway
public record ChatCreators(
        @NotNull
        @ManyToOne(cascade = CascadeType.PERSIST)
        User firstCreator,
        @NotNull
        @ManyToOne(cascade = CascadeType.PERSIST)
        User secondCreator
) implements Serializable {

    public ChatCreators {
        if (firstCreator.getId().compareTo(secondCreator.getId()) > 0) {
            User temp = firstCreator;
            firstCreator = secondCreator;
            secondCreator = temp;
        }
    }
}