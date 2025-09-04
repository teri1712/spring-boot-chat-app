package com.decade.practice.models.local;

import java.util.Objects;

public class FileEvent {

        private String filename;
        private int size;
        private String mediaUrl;

        protected FileEvent() {

        }

        public FileEvent(String filename, int size, String mediaUrl) {
                this.filename = filename;
                this.size = size;
                this.mediaUrl = mediaUrl;
        }

        public String getFilename() {
                return filename;
        }

        public void setFilename(String filename) {
                this.filename = filename;
        }

        public int getSize() {
                return size;
        }

        public void setSize(int size) {
                this.size = size;
        }

        public String getMediaUrl() {
                return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
                this.mediaUrl = mediaUrl;
        }

        @Override
        public boolean equals(Object o) {
                if (!(o instanceof FileEvent fileEvent)) return false;
                return size == fileEvent.size && Objects.equals(filename, fileEvent.filename) && Objects.equals(mediaUrl, fileEvent.mediaUrl);
        }

        @Override
        public int hashCode() {
                return Objects.hash(filename, size, mediaUrl);
        }

        @Override
        public String toString() {
                return "FileEvent{" +
                        "filename='" + filename + '\'' +
                        ", size=" + size +
                        ", mediaUrl='" + mediaUrl + '\'' +
                        '}';
        }
}
