package com.decade.practice.threads.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("IMAGE")
@Getter
public class ImageEvent extends MessageEvent {

    @Valid
    @Column(updatable = false)
    @Embedded
    private ImageSpec image;

    protected ImageEvent() {
    }

    public ImageEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, ImageSpec image) {
        super(senderId, "IMAGE", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.image = image;
    }


    @Override
    public String getMessage() {
        return (isMine() ? "You has sent " : "Sent") + "an image";
    }
}