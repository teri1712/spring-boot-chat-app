package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ImageSpecEmbeddable;
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
    private ImageSpecEmbeddable background;

    protected Theme() {
    }

    public Theme(Integer id, ImageSpecEmbeddable background) {
        this.id = id;
        this.background = background;
    }

}
