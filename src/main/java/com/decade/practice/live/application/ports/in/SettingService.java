package com.decade.practice.live.application.ports.in;

import com.decade.practice.live.application.ports.out.LivenessBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SettingService extends LiveChatTopic {

    @Value("${broker.topics.setting}")
    String settingBrokerTopic;

    public SettingService(LivenessBroker broker) {
        super(broker);
    }

    @Override
    protected String getTopic() {
        return settingBrokerTopic;
    }

    @Override
    protected void onJoin(String chatId, UUID userId, String avatar) {

    }

    @Override
    protected void onLeave(String chatId, UUID userId, String avatar) {

    }
}
