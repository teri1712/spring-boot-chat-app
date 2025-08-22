package com.decade.practice.model.domain.entity;

import jakarta.persistence.Entity;

@Entity
public abstract class MediaEvent extends ChatEvent {

        private String mediaUrl;
        private int size;

        protected MediaEvent(Chat chat, User sender, String eventType) {
                super(chat, sender, eventType);
        }

        protected MediaEvent() {
                super();
        }

        public String getMediaUrl() {
                return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
                this.mediaUrl = mediaUrl;
        }

        public int getSize() {
                return size;
        }

        public void setSize(int size) {
                this.size = size;
        }

}
