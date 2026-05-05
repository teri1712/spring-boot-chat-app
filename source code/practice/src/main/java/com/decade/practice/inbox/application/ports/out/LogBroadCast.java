package com.decade.practice.inbox.application.ports.out;

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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class LogBroadCast {

    protected final LogRepository logs;
    protected final LookUpRegistry lookUpRegistry;
    protected final ConversationRepository conversations;
    protected final DeliveryService deliveryService;
    protected final InboxLogMapper inboxLogMapper;
    protected final ConversationInfoService conversationInfoService;

    protected void broadcastInsert(MessageCreated message, List<ConversationView> convos) {
        Set<UUID> allNeedUsers = new HashSet<>(message.currentState().getAllPartners().toList());


        convos.forEach(conversationView -> {
            Room room = conversationView.room();
            allNeedUsers.addAll(room.getRepresentatives());
            allNeedUsers.add(room.getCreator());
        });
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allNeedUsers);

        convos.forEach(conversationView -> {
            Conversation conversation = conversationView.conversation();
            Room room = conversationView.room();
            UUID ownerId = conversation.getOwnerId();
            InboxLog log = new InboxLog(LogAction.ADDITION,
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

    protected void broadcastUpdate(MessageUpdated message, List<ConversationView> convos) {
        Set<UUID> allNeedUsers = new HashSet<>(message.currentState().getAllPartners().toList());

        convos.forEach(conversationView -> {
            Room room = conversationView.room();
            allNeedUsers.addAll(room.getRepresentatives());
            allNeedUsers.add(room.getCreator());
        });
        PartnerLookUp lookUp = lookUpRegistry.registerLookUp(allNeedUsers);

        convos.forEach(conversationView -> {
            Conversation conversation = conversationView.conversation();
            Room room = conversationView.room();
            UUID ownerId = conversation.getOwnerId();
            InboxLog log = new InboxLog(LogAction.UPDATE,
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

    private void send(InboxLog inboxLog, Room room, Conversation conversation, PartnerLookUp lookUp) {
        UUID ownerId = inboxLog.getOwnerId();
        deliveryService.send(inboxLogMapper.map(inboxLog, lookUp, conversation, conversationInfoService.getInfo(ownerId, room, lookUp)));
    }
}
