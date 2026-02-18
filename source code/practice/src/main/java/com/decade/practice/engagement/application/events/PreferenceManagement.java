package com.decade.practice.engagement.application.events;

import com.decade.practice.engagement.api.events.EventPlacedMapper;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ThemeRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.Theme;
import com.decade.practice.engagement.domain.events.PreferenceParticipantPlaced;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class PreferenceManagement {

    private final ThemeRepository themes;
    private final ChatRepository chats;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventPlacedMapper mapper;


    @EventListener
    public void on(PreferenceParticipantPlaced preferencePlaced) {

        ParticipantId participantId = new ParticipantId(preferencePlaced.senderId(), preferencePlaced.chatId());
        String chatId = participantId.chatId();
        Chat chat = chats.findById(chatId).orElseThrow();

        if (preferencePlaced.iconId() != null)
            chat.updateIcon(preferencePlaced.iconId());

        String theme = Optional.ofNullable(preferencePlaced.themeId())
                .map(themeId -> themes.findById(themeId).orElseThrow())
                .map(Theme::getBackground)
                .orElse(null);
        if (theme != null) {
            chat.updateTheme(theme);
        }
        if (preferencePlaced.roomAvatar() != null)
            chat.updateAvatar(preferencePlaced.roomAvatar());
        if (preferencePlaced.roomName() != null)
            chat.updateRoomName(preferencePlaced.roomName());

        chats.save(chat);

        applicationEventPublisher.publishEvent(mapper.toPrefPlaced(preferencePlaced, chat.getPreference()));
    }

}
