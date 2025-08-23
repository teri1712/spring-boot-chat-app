package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("IMAGE")
public class ImageEvent extends MediaEvent {

        @NotNull
        @Valid
        @Column(updatable = false)
        @Embedded
        private ImageSpec image;

        protected ImageEvent() {
                super();
        }

        public ImageEvent(Chat chat, User sender, ImageSpec image) {
                super(chat, sender, "IMAGE");
                this.image = image;
                setMediaUrl(image.getUri());
        }

        public ImageEvent(ImageEvent event) {
                this(event.getChat(), event.getSender(), event.getImage());
        }

        @Override
        public ChatEvent copy() {
                return new ImageEvent(this);
        }

        @JsonGetter
        public com.decade.practice.model.local.ImageEvent getImageEvent() {
                return new com.decade.practice.model.local.ImageEvent(image);
        }

        public ImageSpec getImage() {
                return image;
        }

        public void setImage(ImageSpec image) {
                this.image = image;
        }
}