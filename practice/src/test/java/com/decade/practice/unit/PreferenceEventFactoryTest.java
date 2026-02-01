package com.decade.practice.unit;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.PreferenceRequest;
import com.decade.practice.application.usecases.PreferenceEventFactory;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PreferenceEventFactoryTest {

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private PreferenceEventFactory factory;

    @Test
    void givenPreferenceEvent_whenGetSupportedType_thenPreferenceEventClassIsReturned() {
        assertEquals(PreferenceEvent.class, factory.getSupportedType());
    }

    @Test
    void givenEventRequest_whenCreateEvent_thenPreferenceEventIsCreated() {
        EventRequest request = new EventRequest();
        ChatIdentifier identifier = new ChatIdentifier(UUID.randomUUID(), UUID.randomUUID());
        request.setChatIdentifier(identifier);

        PreferenceRequest prefReq = new PreferenceRequest();
        prefReq.setRoomName("Test Room");
        prefReq.setIconId(1);
        prefReq.setThemeId(101);
        request.setPreferenceEvent(prefReq);

        Theme theme = mock(Theme.class);
        given(themeRepository.findById(101)).willReturn(Optional.of(theme));

        PreferenceEvent result = factory.createEvent(request);

        assertNotNull(result);
        assertEquals(identifier, result.getChatIdentifier());
        assertEquals("Test Room", result.getPreference().getRoomName());
        assertEquals(theme, result.getPreference().getTheme());
        assertEquals("PREFERENCE", result.getEventType());
    }

    @Test
    void givenPreferenceEvent_whenCreateEventDto_thenEventDtoIsReturned() {
        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setUsername("owner");

        User partner = new User();
        partner.setId(UUID.randomUUID());
        partner.setUsername("partner");

        Chat chat = new Chat(owner, partner);

        Preference preference = new Preference();
        preference.setRoomName("Room Name");
        preference.setIconId(1);

        PreferenceEvent event = new PreferenceEvent(chat, owner, preference);
        event.setId(UUID.randomUUID());

        EventDto result = factory.createEventDto(event);

        assertNotNull(result);
        assertNotNull(result.getPreferenceEvent());
        assertEquals("Room Name", result.getPreferenceEvent().getPreference().getRoomName());
    }
}
