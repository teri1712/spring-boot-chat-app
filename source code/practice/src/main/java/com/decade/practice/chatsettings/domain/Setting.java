package com.decade.practice.chatsettings.domain;

import com.decade.practice.engagement.domain.events.PreferenceChanged;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
public class Setting extends AbstractAggregateRoot<Setting> {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE)
      private Long id;


      @Column(unique = true)
      private String identifier;

      @Embedded
      @NotNull
      private Preference preference;

      private Instant lastActivity;

      public Setting(String identifier, Preference preference) {
            this.identifier = identifier;
            this.preference = preference;
            this.lastActivity = Instant.now();
      }

      public void refreshLastActivity() {
            this.lastActivity = Instant.now();
      }

      protected Setting() {
      }


      public PreferenceChain getPreferenceChain(UUID makerId) {
            return new PreferenceChain(makerId, preference.iconId(), preference.roomName(), preference.roomAvatar(), preference.theme());
      }


      public class PreferenceChain {

            private final UUID makerId;
            private final Integer iconId;
            private final String roomName;
            private final String roomAvatar;
            private final Theme theme;

            public PreferenceChain(UUID makerId, Integer iconId, String roomName, String roomAvatar, Theme theme) {
                  this.makerId = makerId;
                  this.iconId = iconId;
                  this.roomName = roomName;
                  this.roomAvatar = roomAvatar;
                  this.theme = theme;
            }

            public PreferenceChain iconId(Integer iconId) {
                  return new PreferenceChain(makerId, iconId, roomName, roomAvatar, theme);
            }

            public PreferenceChain roomName(String roomName) {
                  return new PreferenceChain(makerId, iconId, roomName, roomAvatar, theme);
            }

            public PreferenceChain roomAvatar(String roomAvatar) {
                  return new PreferenceChain(makerId, iconId, roomName, roomAvatar, theme);
            }

            public PreferenceChain theme(Theme theme) {
                  return new PreferenceChain(makerId, iconId, roomName, roomAvatar, theme);
            }

            public void complete() {
                  preference = new Preference(iconId, roomName, roomAvatar, theme);
                  registerEvent(PreferenceChanged.builder()
                            .iconId(preference.iconId())
                            .roomName(preference.roomName())
                            .roomAvatar(preference.roomAvatar())
                            .theme(Optional.ofNullable(preference.theme()).map(Theme::getBackground).orElse(null))
                            .makerId(makerId)
                            .chatId(getIdentifier())
                            .createdAt(Instant.now())
                            .build());
            }

      }
}