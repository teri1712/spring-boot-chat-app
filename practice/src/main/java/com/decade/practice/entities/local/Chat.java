package com.decade.practice.entities.local;

import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.embeddable.Preference;
import com.decade.practice.entities.domain.entity.User;

import java.util.Objects;
import java.util.UUID;

public class Chat {
      private ChatIdentifier identifier;
      private UUID owner;
      private UUID partner;
      private Preference preference;

      public Chat(ChatIdentifier identifier, UUID owner, Preference preference) {
            this.identifier = identifier;
            this.owner = owner;
            this.partner = identifier.getFirstUser().equals(owner) ?
                  identifier.getSecondUser() : identifier.getFirstUser();
            this.preference = preference;
      }

      protected Chat() {
      }

      public Chat(com.decade.practice.entities.domain.entity.Chat chat, User owner) {
            this(chat.getIdentifier(), owner.getId(), chat.getPreference());
      }

      public void setIdentifier(ChatIdentifier identifier) {
            this.identifier = identifier;
      }

      public void setOwner(UUID owner) {
            this.owner = owner;
      }

      public void setPartner(UUID partner) {
            this.partner = partner;
      }

      public ChatIdentifier getIdentifier() {
            return identifier;
      }

      public UUID getOwner() {
            return owner;
      }

      public UUID getPartner() {
            return partner;
      }

      public Preference getPreference() {
            return preference;
      }

      public void setPreference(Preference preference) {
            this.preference = preference;
      }

      @Override
      public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Chat chat = (Chat) o;
            return Objects.equals(identifier, chat.identifier) &&
                  Objects.equals(owner, chat.owner) &&
                  Objects.equals(partner, chat.partner);
      }

      @Override
      public int hashCode() {
            return Objects.hash(identifier, owner, partner);
      }

      @Override
      public String toString() {
            return "Chat{" +
                  "identifier=" + identifier +
                  ", owner=" + owner +
                  ", partner=" + partner +
                  '}';
      }
}