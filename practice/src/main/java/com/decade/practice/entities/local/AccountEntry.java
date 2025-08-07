package com.decade.practice.entities.local;

import com.decade.practice.entities.domain.ChatSnapshot;

import java.util.List;
import java.util.Objects;

public class AccountEntry {
      private Account account;
      private List<ChatSnapshot> chatSnapshots;

      public AccountEntry(Account account, List<ChatSnapshot> chatSnapshots) {
            this.account = account;
            this.chatSnapshots = chatSnapshots;
      }

      protected AccountEntry() {
      }

      public Account getAccount() {
            return account;
      }

      public List<ChatSnapshot> getChatSnapshots() {
            return chatSnapshots;
      }

      public void setAccount(Account account) {
            this.account = account;
      }

      public void setChatSnapshots(List<ChatSnapshot> chatSnapshots) {
            this.chatSnapshots = chatSnapshots;
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