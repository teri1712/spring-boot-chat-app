package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Theme {

    @Id
    private Integer id;

    @Embedded
    private ImageSpec background;

    protected Theme() {
    }

    public Theme(Integer id, ImageSpec background) {
        this.id = id;
        this.background = background;
    }

}
