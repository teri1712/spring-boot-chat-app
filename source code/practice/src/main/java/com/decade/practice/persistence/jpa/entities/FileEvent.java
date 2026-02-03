package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("FILE")
@Getter
@Setter
@NoArgsConstructor
public class FileEvent extends MediaEvent {
    private String filename;

    public FileEvent(Chat chat, User sender, String mediaUrl, String filename, int size) {
        super(chat, sender, "FILE");
        setMediaUrl(mediaUrl);
        setSize(size);
        this.filename = filename;
    }


    @Override
    public ChatEvent clone() {
        return new FileEvent(getChat(), getSender(), getMediaUrl(), filename, getSize());
    }
}
