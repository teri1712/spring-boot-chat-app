package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.MessageStateResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LogBroadCaster {

    final LogRepository logs;
    final ConversationRepository conversations;
    final DeliveryService deliveryService;
    final MessageStateResponseMapper messageStateMapper;
    final ConversationInfoService conversationInfoService;

    @Transactional
    public void broadcastInsert(MessageCreated message, List<ConversationView> convos) {
        convos.forEach(cv -> {
            Conversation conversation = cv.conversation();
            Room room = cv.room();
            UUID ownerId = conversation.getOwnerId();
            InboxLog log = new InboxLog(LogAction.ADDITION, message.senderId(), ownerId, conversation.getId(), message.id(), message.currentState());
            logs.save(log);

            conversation.addRecent(message.currentState());
            conversations.save(conversation);

            var info = conversationInfoService.getInfo(ownerId, room);
            deliveryService.send(new InboxLogMessage(
                log.getSequenceId(),
                room.getChatId(),
                info,
                conversation.getHash().value(),
                log.getSenderId(),
                ownerId,
                log.getAction(),
                messageStateMapper.toResponse(log.getMessageState())
            ));

        });
    }

    @Transactional
    public void broadcastUpdate(MessageUpdated message, List<ConversationView> convos) {
        convos.forEach(cv -> {
            Conversation conversation = cv.conversation();
            Room room = cv.room();
            UUID ownerId = conversation.getOwnerId();
            InboxLog log = new InboxLog(LogAction.UPDATE, message.senderId(), ownerId, conversation.getId(), message.id(), message.currentState());
            logs.save(log);
            conversation.updateRecent(message.currentState());
            conversations.save(conversation);

            var info = conversationInfoService.getInfo(ownerId, room);
            deliveryService.send(new InboxLogMessage(
                log.getSequenceId(),
                room.getChatId(),
                info,
                conversation.getHash().value(),
                log.getSenderId(),
                ownerId,
                log.getAction(),
                messageStateMapper.toResponse(log.getMessageState())
            ));

        });
    }

}
