package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.PreferenceCreateCommand;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PreferenceEventFactory extends EventFactory<PreferenceEvent> {

    private final ThemeRepository themeRepository;

    protected PreferenceEventFactory(UserRepository userRepository, ChatRepository chatRepository, ThemeRepository themeRepository) {
        super(userRepository, chatRepository);
        this.themeRepository = themeRepository;
    }

    @Override
    public PreferenceEvent newInstance(EventCreateCommand command) {
        PreferenceCreateCommand preferenceCreateCommand = (PreferenceCreateCommand) command;
        Preference preference = new Preference();
        preference.setRoomName(preferenceCreateCommand.getRoomName());
        preference.setIconId(preferenceCreateCommand.getIconId());
        if (preferenceCreateCommand.getThemeId() != null)
            preference.setTheme(themeRepository.findById(preferenceCreateCommand.getThemeId()).orElseThrow());
        PreferenceEvent preferenceEvent = new PreferenceEvent();
        preferenceEvent.setPreference(preference);
        preferenceEvent.setEventType("PREFERENCE");
        return preferenceEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof PreferenceCreateCommand;
    }
}
