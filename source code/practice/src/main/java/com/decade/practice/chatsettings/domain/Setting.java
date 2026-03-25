package com.decade.practice.chatsettings.domain;

import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

      public Setting(String identifier, Preference preference) {
            this.identifier = identifier;
            this.preference = preference;
      }


      protected Setting() {
      }


      public PreferenceChain getPreferenceChain(UUID makerId) {
            return new PreferenceChain(makerId, preference.iconId(), preference.customName(), preference.customAvatar(), preference.theme());
      }


      public class PreferenceChain {

            private final UUID makerId;
            private final Integer iconId;
            private final String customName;
            private final String customAvatar;
            private final Theme theme;

            public PreferenceChain(UUID makerId, Integer iconId, String customName, String customAvatar, Theme theme) {
                  this.makerId = makerId;
                  this.iconId = iconId;
                  this.customName = customName;
                  this.customAvatar = customAvatar;
                  this.theme = theme;
            }

            public PreferenceChain iconId(Integer iconId) {
                  return new PreferenceChain(makerId, iconId, customName, customAvatar, theme);
            }

            public PreferenceChain roomName(String roomName) {
                  return new PreferenceChain(makerId, iconId, roomName, customAvatar, theme);
            }

            public PreferenceChain roomAvatar(String roomAvatar) {
                  return new PreferenceChain(makerId, iconId, customName, roomAvatar, theme);
            }

            public PreferenceChain theme(Theme theme) {
                  return new PreferenceChain(makerId, iconId, customName, customAvatar, theme);
            }

            public void complete() {
                  preference = new Preference(iconId, customName, customAvatar, theme);
                  log.debug("Preference changed: {}", preference);
                  registerEvent(PreferenceChanged.builder()
                            .iconId(preference.iconId())
                            .customName(preference.customName())
                            .customAvatar(preference.customAvatar())
                            .theme(Optional.ofNullable(preference.theme()).map(Theme::getBackground).orElse(null))
                            .makerId(makerId)
                            .chatId(getIdentifier())
                            .createdAt(Instant.now())
                            .build());
            }

      }
}