package com.decade.practice.model.local;

import com.decade.practice.model.domain.ChatSnapshot;

import java.util.List;
import java.util.Objects;

public class AccountEntry {
    private final Account account;
    private final List<ChatSnapshot> chatSnapshots;

    public AccountEntry(Account account, List<ChatSnapshot> chatSnapshots) {
        this.account = account;
        this.chatSnapshots = chatSnapshots;
    }

    public Account getAccount() {
        return account;
    }

    public List<ChatSnapshot> getChatSnapshots() {
        return chatSnapshots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntry that = (AccountEntry) o;
        return Objects.equals(account, that.account) &&
                Objects.equals(chatSnapshots, that.chatSnapshots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, chatSnapshots);
    }

    @Override
    public String toString() {
        return "AccountEntry{" +
                "account=" + account +
                ", chatSnapshots=" + chatSnapshots +
                '}';
    }
}