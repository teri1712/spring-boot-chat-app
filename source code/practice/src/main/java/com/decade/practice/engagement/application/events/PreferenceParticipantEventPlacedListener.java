package com.decade.practice.engagement.application.events;

import com.decade.practice.engagement.api.events.EventPlacedMapper;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.application.ports.out.ThemeRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ParticipantId;
import com.decade.practice.engagement.domain.Theme;
import com.decade.practice.engagement.domain.events.PreferenceParticipantPlaced;
import com.decade.practice.engagement.domain.events.ProcessedPreferenceParticipantPlaced;
import com.decade.practice.engagement.domain.services.EngagementPolicy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PreferenceParticipantEventPlacedListener extends AbstractParticipantEventPlacedListener<ProcessedPreferenceParticipantPlaced> {

    private final ThemeRepository themes;


    public PreferenceParticipantEventPlacedListener(ChatRepository chats, ParticipantRepository participants, ApplicationEventPublisher applicationEventPublisher, EngagementPolicy engagementPolicy, EventPlacedMapper mapper, ThemeRepository themes) {
        super(chats, participants, applicationEventPublisher, engagementPolicy, mapper);
        this.themes = themes;
    }

    @Override
    protected void handle(ProcessedPreferenceParticipantPlaced preferencePlaced) {

        ParticipantId participantId = new ParticipantId(preferencePlaced.getSenderId(), preferencePlaced.getChatId());
        String chatId = participantId.chatId();
        Chat chat = chats.findById(chatId).orElseThrow();

        if (preferencePlaced.getIconId() != null)
            chat.updateIcon(preferencePlaced.getIconId());
        if (preferencePlaced.getThemeId() != null) {
            chat.updateTheme(preferencePlaced.getTheme());
        }
        if (preferencePlaced.getRoomAvatar() != null)
            chat.updateAvatar(preferencePlaced.getRoomAvatar());
        if (preferencePlaced.getRoomName() != null)
            chat.updateRoomName(preferencePlaced.getRoomName());

        chats.save(chat);
    }


    @EventListener
    public void on(PreferenceParticipantPlaced preferencePlaced) {

        ParticipantId participantId = new ParticipantId(preferencePlaced.getSenderId(), preferencePlaced.getChatId());
        String chatId = participantId.chatId();
        Chat chat = chats.findById(chatId).orElseThrow();

        if (preferencePlaced.getIconId() != null)
            chat.updateIcon(preferencePlaced.getIconId());

        String theme = Optional.ofNullable(preferencePlaced.getThemeId())
                .map(themeId -> themes.findById(themeId).orElseThrow())
                .map(Theme::getBackground)
                .orElse(null);
        if (theme != null) {
            chat.updateTheme(theme);
        }
        if (preferencePlaced.getRoomAvatar() != null)
            chat.updateAvatar(preferencePlaced.getRoomAvatar());
        if (preferencePlaced.getRoomName() != null)
            chat.updateRoomName(preferencePlaced.getRoomName());

        chats.save(chat);

        applicationEventPublisher.publishEvent(mapper.toPref(preferencePlaced, theme));
    }
}
