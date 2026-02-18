package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ChatSnapshot;
import com.decade.practice.engagement.api.events.PreferenceEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.MessageEvent;
import com.decade.practice.threads.domain.PreferenceEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreferenceEventPlacedListener extends AbstractEventPlacedListener<PreferenceEventPlaced> {

    public PreferenceEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(PreferenceEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(PreferenceEventPlaced eventPlaced, ChatSnapshot snapshot, UUID ownerId) {
        return new PreferenceEvent(eventPlaced.getSenderId(), ownerId, snapshot.chatId(), snapshot.roomName(), snapshot.roomAvatar(), eventPlaced.getIconId(), eventPlaced.getRoomName(), eventPlaced.getRoomAvatar(), eventPlaced.getTheme());
    }
}
