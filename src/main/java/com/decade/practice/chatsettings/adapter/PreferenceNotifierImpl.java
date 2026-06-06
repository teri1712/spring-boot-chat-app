package com.decade.practice.chatsettings.adapter;

import com.decade.practice.chatsettings.application.ports.out.PreferenceNotifier;
import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
@Component
public class PreferenceNotifierImpl implements PreferenceNotifier {

    @Value("${broker.topics.setting}")
    private String preferenceTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void notify(String chatId, PreferenceMessage message) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisTemplate.convertAndSend(preferenceTopic + ":" + chatId, message);
                }
            });
        } else {
            redisTemplate.convertAndSend(preferenceTopic + ":" + chatId, message);
        }
    }
}
