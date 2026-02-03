package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("IMAGE")
@Getter
@Setter
@NoArgsConstructor
public class ImageEvent extends MediaEvent {

    @NotNull
    @Valid
    @Column(updatable = false)
    @Embedded
    private ImageSpec image;

    public ImageEvent(Chat chat, User sender, ImageSpec image) {
        super(chat, sender, "IMAGE");
        this.image = image;
        setMediaUrl(image.getUri());
    }

    public ImageEvent(ImageEvent event) {
        this(event.getChat(), event.getSender(), event.getImage());
    }

    @Override
    public ChatEvent clone() {
        return new ImageEvent(this);
    }

}