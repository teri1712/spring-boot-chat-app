package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.PreferenceEventDto;
import com.decade.practice.dto.PreferenceResponse;
import com.decade.practice.dto.ThemeDto;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import org.springframework.stereotype.Component;

@Component
public class PreferenceEventConverter extends AbstractEventConverter<PreferenceEvent> {

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
    public Class<PreferenceEvent> supports() {
        return PreferenceEvent.class;
    }
}
