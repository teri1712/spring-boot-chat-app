package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.PreferenceRequest;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreferenceEventFactory implements EventFactory<PreferenceEvent> {

    private final ThemeRepository themeRepository;

    @Override
    public PreferenceEvent newInstance(EventRequest eventRequest) {
        PreferenceRequest preferenceReq = eventRequest.getPreferenceEvent();
        Preference preference = new Preference();
        preference.setRoomName(preferenceReq.roomName());
        preference.setIconId(preferenceReq.iconId());
        if (preferenceReq.themeId() != null)
            preference.setTheme(themeRepository.findById(preferenceReq.themeId()).orElseThrow());

        PreferenceEvent preferenceEvent = new PreferenceEvent();
        preferenceEvent.setPreference(preference);
        preferenceEvent.setEventType("PREFERENCE");
        return preferenceEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getPreferenceEvent() != null;
    }
}
