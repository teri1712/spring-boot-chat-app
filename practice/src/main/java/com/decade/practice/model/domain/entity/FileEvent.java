package com.decade.practice.model.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILE")
public class FileEvent extends MediaEvent {
        private String filename;

        public FileEvent(Chat chat, User sender, String mediaUrl, String filename, int size) {
                super(chat, sender, "FILE");
                setMediaUrl(mediaUrl);
                setSize(size);
                this.filename = filename;
        }

        protected FileEvent() {
        }

        public String getFilename() {
                return filename;
        }

        @JsonGetter
        public com.decade.practice.model.local.FileEvent getFileEvent() {
                return new com.decade.practice.model.local.FileEvent(filename, getSize(), getMediaUrl());
        }

        @Override
        public ChatEvent copy() {
                return new FileEvent(getChat(), getSender(), getMediaUrl(), filename, getSize());
        }
}
