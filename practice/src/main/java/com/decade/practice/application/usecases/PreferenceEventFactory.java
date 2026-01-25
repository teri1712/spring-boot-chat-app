package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.PreferenceDto;
import com.decade.practice.api.dto.PreferenceEventDto;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PreferenceEventFactory extends AbstractEventFactory<PreferenceEvent> {

    private final ThemeRepository themeRepository;

    @Override
    protected EventDto postInitEventResponse(PreferenceEvent chatEvent, EventDto res) {
        PreferenceDto preference = new PreferenceDto();
        preference.setRoomName(chatEvent.getPreference().getRoomName());
        preference.setResourceId(chatEvent.getPreference().getResourceId());
        preference.setThemeId(Optional.ofNullable(chatEvent.getPreference().getTheme()).map(Theme::getId).orElse(null));
        res.setPreferenceEvent(new PreferenceEventDto(preference));
        return res;
    }

    @Override
    public Class<PreferenceEvent> getSupportedType() {
        return PreferenceEvent.class;
    }

    @Override
    public PreferenceEvent createEvent(EventRequest eventRequest) {
        PreferenceDto preferenceDto = eventRequest.getPreferenceEvent().getPreference();
        Preference preference = new Preference();
        preference.setRoomName(preferenceDto.getRoomName());
        preference.setResourceId(preferenceDto.getResourceId());
        preference.setTheme(Optional.ofNullable(preferenceDto.getThemeId()).flatMap(new Function<Integer, Optional<Theme>>() {
            @Override
            public Optional<Theme> apply(Integer integer) {
                return themeRepository.findById(integer);
            }
        }).orElse(null));
        PreferenceEvent preferenceEvent = new PreferenceEvent();
        preferenceEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        preferenceEvent.setPreference(preference);
        preferenceEvent.setEventType("PREFERENCE");
        return preferenceEvent;
    }


}
