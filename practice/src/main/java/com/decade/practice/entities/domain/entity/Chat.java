package com.decade.practice.entities.domain.entity;

import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.embeddable.Preference;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.generator.EventType;

import java.util.Date;
import java.util.Objects;

@Entity
public class Chat {

      @ManyToOne(cascade = CascadeType.PERSIST)
      @JoinColumn(name = "first_user") // for naming
      @MapsId("firstUser")
      private User firstUser;

      @ManyToOne(cascade = CascadeType.PERSIST)
      @JoinColumn(name = "second_user") // for naming
      @MapsId("secondUser")
      private User secondUser;

      @EmbeddedId
      private ChatIdentifier identifier;

      @Embedded
      private Preference preference;


      @CurrentTimestamp(event = EventType.INSERT, source = SourceType.VM)
      @Temporal(TemporalType.TIMESTAMP)
      private Date interactTime;

      private int messageCount = 0;

      // for un-saved check
      @Version
      private Integer version;

      // No-arg constructor required by JPA
      protected Chat() {
      }

      public Chat(User firstUser, User secondUser) {
            // Ensure firstUser.id is less than secondUser.id
            if (firstUser.getId().compareTo(secondUser.getId()) > 0) {
                  User temp = firstUser;
                  firstUser = secondUser;
                  secondUser = temp;
            }
            this.firstUser = firstUser;
            this.secondUser = secondUser;
            this.identifier = new ChatIdentifier(firstUser.getId(), secondUser.getId());
      }

      public User getFirstUser() {
            return firstUser;
      }

      public void setFirstUser(User firstUser) {
            this.firstUser = firstUser;
      }

      public User getSecondUser() {
            return secondUser;
      }

      public void setSecondUser(User secondUser) {
            this.secondUser = secondUser;
      }

      public ChatIdentifier getIdentifier() {
            return identifier;
      }

      public void setIdentifier(ChatIdentifier identifier) {
            this.identifier = identifier;
      }

      public Date getInteractTime() {
            return interactTime;
      }

      public void setInteractTime(Date interactTime) {
            this.interactTime = interactTime;
      }

      public int getMessageCount() {
            return messageCount;
      }

      public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
      }

      public Integer getVersion() {
            return version;
      }

      public void setVersion(Integer version) {
            this.version = version;
      }

      @Override
      public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Chat chat = (Chat) o;
            return Objects.equals(identifier, chat.identifier);
      }

      public Preference getPreference() {
            return preference;
      }

      public void setPreference(Preference preference) {
            this.preference = preference;
      }

      @Override
      public int hashCode() {
            return Objects.hash(identifier);
      }

      @Override
      public String toString() {
            return "Chat{" +
                  "identifier=" + identifier +
                  ", messageCount=" + messageCount +
                  '}';
      }
}