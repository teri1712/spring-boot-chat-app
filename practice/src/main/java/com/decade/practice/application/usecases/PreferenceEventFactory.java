package com.decade.practice.application.usecases;

import com.decade.practice.dto.*;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreferenceEventFactory extends AbstractEventFactory<PreferenceEvent> {

    private final ThemeRepository themeRepository;

    @Override
    protected EventDto postInitEventResponse(PreferenceEvent chatEvent, EventDto res) {
        PreferenceResponse preference = new PreferenceResponse();
        preference.setRoomName(chatEvent.getPreference().getRoomName());
        preference.setIconId(chatEvent.getPreference().getIconId());
        if (chatEvent.getPreference().getTheme() != null)
            preference.setTheme(ThemeDto.from(chatEvent.getPreference().getTheme()));
        res.setPreferenceEvent(new PreferenceEventDto(preference));
        return res;
    }

    @Override
    public Class<PreferenceEvent> getSupportedType() {
        return PreferenceEvent.class;
    }

    @Override
    public PreferenceEvent createEvent(EventRequest eventRequest) {
        PreferenceRequest preferenceReq = eventRequest.getPreferenceEvent();
        Preference preference = new Preference();
        preference.setRoomName(preferenceReq.getRoomName());
        preference.setIconId(preferenceReq.getIconId());
        if (preferenceReq.getThemeId() != null)
            preference.setTheme(themeRepository.findById(preferenceReq.getThemeId()).orElseThrow());

        PreferenceEvent preferenceEvent = new PreferenceEvent();
        preferenceEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        preferenceEvent.setPreference(preference);
        preferenceEvent.setEventType("PREFERENCE");
        return preferenceEvent;
    }


}
