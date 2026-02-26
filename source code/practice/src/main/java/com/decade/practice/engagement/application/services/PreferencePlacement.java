package com.decade.practice.engagement.application.services;

import com.decade.practice.engagement.application.ports.in.PreferenceCommand;
import com.decade.practice.engagement.application.ports.out.ChatEventRepository;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.application.ports.out.ThemeRepository;
import com.decade.practice.engagement.domain.ChatEvent;
import com.decade.practice.engagement.domain.PreferenceChatEvent;
import com.decade.practice.engagement.domain.Theme;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import com.decade.practice.engagement.dto.mapper.ChatEventMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class PreferencePlacement extends AbstractParticipantPlacement<PreferenceCommand> {
      private final ThemeRepository themes;

      public PreferencePlacement(ChatEventRepository events, ChatEventMapper chatEventMapper, ParticipantRepository participants, EngagementPolicy engagementPolicy, ChatRepository chats, ThemeRepository themes) {
            super(events, chatEventMapper, participants, engagementPolicy, chats);
            this.themes = themes;
      }


      @Override
      protected ChatEvent newInstance(PreferenceCommand participantCommand) {
            String theme = Optional.ofNullable(participantCommand.getThemeId()).flatMap(new Function<Long, Optional<Theme>>() {
                  @Override
                  public Optional<Theme> apply(Long themeId) {
                        return themes.findById(themeId);
                  }
            }).map(Theme::getBackground).orElse(null);
            return new PreferenceChatEvent(
                      participantCommand.getIdempotentKey(),
                      participantCommand.getChatId(),
                      participantCommand.getSenderId(),
                      participantCommand.getIconId(),
                      participantCommand.getRoomName(),
                      participantCommand.getRoomAvatar(),
                      theme
            );
      }
}
