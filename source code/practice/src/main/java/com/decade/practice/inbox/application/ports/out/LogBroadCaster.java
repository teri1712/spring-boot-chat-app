package com.decade.practice.inbox.application.ports.out;

import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.InboxLog;
import com.decade.practice.inbox.domain.LogAction;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.MessageStateResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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
        List<InboxLog> logsToSave = new ArrayList<>(convos.size());
        List<Conversation> convosToSave = new ArrayList<>(convos.size());

        log.info("Broadcasting insert for {} convos", convos.size());
        for (ConversationView cv : convos) {
            Conversation conversation = cv.conversation();
            UUID ownerId = conversation.getOwnerId();

            InboxLog log = new InboxLog(LogAction.ADDITION, message.senderId(), ownerId, conversation.getId(), message.id());
            logsToSave.add(log);

            conversation.addRecent(message.currentState());
            convosToSave.add(conversation);
        }

        logs.saveAll(logsToSave);
        conversations.saveAll(convosToSave);

        for (int i = 0; i < convos.size(); i++) {
            ConversationView cv = convos.get(i);
            InboxLog log = logsToSave.get(i);
            Conversation conversation = convosToSave.get(i);

            var info = conversationInfoService.getInfo(conversation.getOwnerId(), cv.room());
            deliveryService.send(new InboxLogMessage(
                log.getSequenceId(),
                cv.room().getChatId(),
                info,
                conversation.getHash().value(),
                log.getSenderId(),
                conversation.getOwnerId(),
                log.getAction(),
                messageStateMapper.toResponse(message.currentState())
            ));
        }
    }

    @Transactional
    public void broadcastUpdate(MessageUpdated message, List<ConversationView> convos) {
        List<InboxLog> logsToSave = new ArrayList<>(convos.size());
        List<Conversation> convosToSave = new ArrayList<>(convos.size());

        for (ConversationView cv : convos) {
            Conversation conversation = cv.conversation();
            UUID ownerId = conversation.getOwnerId();

            InboxLog log = new InboxLog(LogAction.UPDATE, message.senderId(), ownerId, conversation.getId(), message.id());
            logsToSave.add(log);

            conversation.updateRecent(message.currentState());
            convosToSave.add(conversation);
        }

        logs.saveAll(logsToSave);
        conversations.saveAll(convosToSave);

        for (int i = 0; i < convos.size(); i++) {
            ConversationView cv = convos.get(i);
            InboxLog log = logsToSave.get(i);
            Conversation conversation = convosToSave.get(i);

            var info = conversationInfoService.getInfo(conversation.getOwnerId(), cv.room());
            deliveryService.send(new InboxLogMessage(
                log.getSequenceId(),
                cv.room().getChatId(),
                info,
                conversation.getHash().value(),
                log.getSenderId(),
                conversation.getOwnerId(),
                log.getAction(),
                messageStateMapper.toResponse(message.currentState())
            ));
        }
    }

}
