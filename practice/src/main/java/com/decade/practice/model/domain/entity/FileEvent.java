package com.decade.practice.model.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILE")
public class FileEvent extends MediaEvent {
        private String filename;
        private int size;

        public FileEvent(Chat chat, User sender, String url, String filename) {
                super(chat, sender, "FILE");
                setMediaUrl(url);
                this.filename = filename;
        }

        protected FileEvent() {
        }

        public String getFilename() {
                return filename;
        }

        public int getSize() {
                return size;
        }


        @Override
        public ChatEvent copy() {
                return new FileEvent(getChat(), getSender(), getMediaUrl(), filename);
        }
}
