package com.decade.practice.models.local;

import com.decade.practice.models.TokenCredential;
import com.decade.practice.models.domain.entity.SyncContext;
import com.decade.practice.models.domain.entity.User;

import java.util.Objects;
import java.util.UUID;

public class Account {

        private UUID id;
        private User user;
        private String username;
        private TokenCredential credential;
        private SyncContext syncContext;

        public Account(
                UUID id,
                String username,
                User user,
                TokenCredential credential,
                SyncContext syncContext) {
                this.id = id;
                this.username = username;
                this.user = user;
                this.credential = credential;
                this.syncContext = syncContext;
        }

        protected Account() {
        }

        public Account(User user, TokenCredential credential) {
                this(
                        user.getId(),
                        user.getUsername(),
                        user,
                        credential,
                        user.getSyncContext()
                );
        }

        public UUID getId() {
                return id;
        }

        public String getUsername() {
                return username;
        }

        public User getUser() {
                return user;
        }

        public TokenCredential getCredential() {
                return credential;
        }

        public SyncContext getSyncContext() {
                return syncContext;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Account account = (Account) o;
                return Objects.equals(id, account.id) &&
                        Objects.equals(username, account.username) &&
                        Objects.equals(user, account.user) &&
                        Objects.equals(credential, account.credential) &&
                        Objects.equals(syncContext, account.syncContext);
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public void setCredential(TokenCredential credential) {
                this.credential = credential;
        }

        public void setSyncContext(SyncContext syncContext) {
                this.syncContext = syncContext;
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, username, user, credential, syncContext);
        }

        @Override
        public String toString() {
                return "Account{" +
                        "id=" + id +
                        ", username='" + username + '\'' +
                        ", user=" + user +
                        ", credential=" + credential +
                        ", syncContext=" + syncContext +
                        '}';
        }
}