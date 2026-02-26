package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.application.events.ChatCreated;
import com.decade.practice.engagement.domain.events.VersionIncremented;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.UUID;

@Entity
@Getter
public class Chat extends AbstractAggregateRoot<Chat> {

      @Embedded
      private ChatCreators creators;

      @Id
      private String identifier;

      @Embedded
      @NotNull
      private Preference preference;

      @Embedded
      private ChatPolicy policy;

      public Chat(ChatCreators creators, String identifier, Preference preference, ChatPolicy policy) {
            this.creators = creators;
            this.identifier = identifier;
            this.preference = preference;
            this.policy = policy;
            registerEvent(new ChatCreated(identifier, preference.roomName(), preference.roomAvatar(),
                      creators.members().toList(), creators.callerId()));
      }


      @Version
      private Integer eventVersion;

      public void increment(UUID eventId) {
            registerEvent(new VersionIncremented(eventId, getIdentifier(), getEventVersion()));
      }

      protected Chat() {
      }

      public Chat updateIcon(Integer iconId) {
            this.preference = new Preference(iconId, preference.roomName(), preference.roomAvatar(), preference.theme());
            return this;
      }

      public Chat updateRoomName(String roomName) {
            this.preference = new Preference(preference.iconId(), roomName, preference.roomAvatar(), preference.theme());
            return this;
      }

      public Chat updateTheme(String theme) {
            this.preference = new Preference(preference.iconId(), preference.roomName(), preference.roomAvatar(), theme);
            return this;
      }

      public Chat updateAvatar(String roomAvatar) {
            this.preference = new Preference(preference.iconId(), preference.roomName(), roomAvatar, preference.theme());
            return this;
      }
}