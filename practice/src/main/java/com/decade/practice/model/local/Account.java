package com.decade.practice.model.local;

import com.decade.practice.model.TokenCredential;
import com.decade.practice.model.domain.SyncContext;
import com.decade.practice.model.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@JsonSerialize
public class Account {
    private final UUID id;
    private final String username;
    private final User user;
    private final TokenCredential credential;
    private final SyncContext syncContext;

    public Account(UUID id, String username, User user, TokenCredential credential, SyncContext syncContext) {
        this.id = id;
        this.username = username;
        this.user = user;
        this.credential = credential;
        this.syncContext = syncContext;
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