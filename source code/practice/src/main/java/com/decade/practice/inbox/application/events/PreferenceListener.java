package com.decade.practice.inbox.application.events;

import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.Preference;
import com.decade.practice.inbox.domain.Room;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PreferenceListener {


    private final MessageRepository messages;
    private final RoomRepository rooms;


    @ApplicationModuleListener(id = "preference_listener")
    public void on(PreferenceChanged event) {

        Room room = rooms.findByChatId(event.getChatId()).orElseThrow();
        room.update(event.getCustomName(), event.getCustomAvatar());
        room.refreshLastActivity();
        rooms.save(room);

        messages.save(new Preference(
            UUID.randomUUID(),
            event.getMakerId(),
            event.getChatId(),
            event.getCreatedAt()));
    }
}
