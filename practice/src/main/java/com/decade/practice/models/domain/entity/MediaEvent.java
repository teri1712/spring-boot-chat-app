package com.decade.practice.models.domain.entity;

import jakarta.persistence.Entity;

@Entity
public abstract class MediaEvent extends MessageEvent {

        private String mediaUrl;
        private int size;

        protected MediaEvent(Chat chat, User sender, String eventType) {
                super(chat, sender, eventType);
        }

        protected MediaEvent() {
                super();
        }

        @Override
        protected void bindExtraProperties() {
                super.bindExtraProperties();
                extraProperties.put("mediaUrl", mediaUrl);
                extraProperties.put("size", size);
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
