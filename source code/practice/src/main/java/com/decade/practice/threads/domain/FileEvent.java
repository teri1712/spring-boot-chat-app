package com.decade.practice.threads.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("FILE")
@Getter
public class FileEvent extends MessageEvent {
    private String filename;
    // TODO: Adjust client
    private String uri;
    private Integer size;

    public FileEvent(UUID senderId, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot, String filename, String uri, Integer size) {
        super(senderId, "FILE", ownerId, chatId, roomNameSnapshot, roomAvatarSnapshot);
        this.filename = filename;
        this.uri = uri;
        this.size = size;
    }

    protected FileEvent() {
    }


    @Override
    public String getMessage() {
        return (isMine() ? "You has sent " : "Sent ") + "a file";
    }
}
