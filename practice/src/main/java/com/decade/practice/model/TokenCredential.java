package com.decade.practice.model;

import java.util.Objects;

public class TokenCredential {
        private String accessToken;
        private String refreshToken;
        private long expiresIn;
        private long createdAt;

        public TokenCredential() {
        }

        public TokenCredential(String accessToken, String refreshToken, long expiresIn, long createdAt) {
                this.accessToken = accessToken;
                this.refreshToken = refreshToken;
                this.expiresIn = expiresIn;
                this.createdAt = createdAt;
        }

        public String getAccessToken() {
                return accessToken;
        }

        public void setAccessToken(String accessToken) {
                this.accessToken = accessToken;
        }

        public String getRefreshToken() {
                return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
        }

        public long getExpiresIn() {
                return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
                this.expiresIn = expiresIn;
        }

        public long getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TokenCredential that = (TokenCredential) o;
                return expiresIn == that.expiresIn &&
                        createdAt == that.createdAt &&
                        Objects.equals(accessToken, that.accessToken) &&
                        Objects.equals(refreshToken, that.refreshToken);
        }

        @Override
        public int hashCode() {
                return Objects.hash(accessToken, refreshToken, expiresIn, createdAt);
        }

        @Override
        public String toString() {
                return "TokenCredential{" +
                        "accessToken='" + accessToken + '\'' +
                        ", refreshToken='" + refreshToken + '\'' +
                        ", expiresIn=" + expiresIn +
                        ", createdAt=" + createdAt +
                        '}';
        }
}