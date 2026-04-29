package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.*;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BatchRoundRobinSaver {

    private final Integer lowerBound;
    private final Integer upperBound;
    private final LogRepository logs;
    private final ConversationRepository conversations;
    private final LookUpRegistry lookUpRegistry;

    private final ConversationInfoService conversationInfoService;
    private final DeliveryService deliveryService;
    private final InboxLogMapper inboxLogMapper;


    @ApplicationModuleListener(id = "batch_insert")
    protected void on(MessageCreated message) {
        doInsert(message);
    }

    private void doInsert(MessageCreated message) {
        Set<UUID> allNeedUsers = new HashSet<>(message.currentState().getAllPartners().toList());
        List<ConversationView> convos = conversations.findByChatIdBetweenRoundRobin(message.chatId(), lowerBound, upperBound);


        convos.forEach(conversationView -> {
            Room room = conversationView.room();
            allNeedUsers.addAll(room.getRepresentatives());
            allNeedUsers.add(room.getCreator());
        });
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allNeedUsers);

        convos.forEach(conversationView -> {
            Conversation conversation = conversationView.conversation();
            Room room = conversationView.room();
            UUID ownerId = conversation.getConversationId().ownerId();
            InboxLog log = new InboxLog(LogAction.ADDITION,
                message.chatId(),
                message.senderId(),
                ownerId,
                conversation.getId(),
                message.id(),
                message.currentState());
            logs.save(log);
            conversation.addRecent(message.currentState());

            conversations.save(conversation);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(log, room, conversation, lookUp);
                }
            });
        });

    }

    @ApplicationModuleListener(id = "batch_update")
    protected void on(MessageUpdated message) {
        doUpdate(message);
    }

    private void doUpdate(MessageUpdated message) {
        Set<UUID> allNeedUsers = new HashSet<>(message.currentState().getAllPartners().toList());
        List<ConversationView> convos = conversations.findByChatIdBetweenRoundRobin(message.chatId(), lowerBound, upperBound);

        convos.forEach(conversationView -> {
            Room room = conversationView.room();
            allNeedUsers.addAll(room.getRepresentatives());
            allNeedUsers.add(room.getCreator());
        });
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allNeedUsers);

        convos.forEach(conversationView -> {
            Conversation conversation = conversationView.conversation();
            Room room = conversationView.room();
            UUID ownerId = conversation.getConversationId().ownerId();
            InboxLog log = new InboxLog(LogAction.UPDATE,
                message.chatId(),
                message.senderId(),
                ownerId,
                conversation.getId(),
                message.id(),
                message.currentState());
            logs.save(log);
            conversation.updateRecent(message.currentState());
            conversations.save(conversation);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(log, room, conversation, lookUp);
                }
            });
        });
    }

    public void send(InboxLog inboxLog, Room room, Conversation conversation, PartnerLookUp lookUp) {
        UUID ownerId = inboxLog.getOwnerId();
        deliveryService.send(inboxLogMapper.map(inboxLog, lookUp, conversation, conversationInfoService.getInfo(ownerId, room, lookUp)));
    }

}
