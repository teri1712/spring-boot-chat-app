package com.decade.practice.inbox.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.application.query.MessageService;
import com.decade.practice.inbox.domain.Message;
import com.decade.practice.inbox.dto.MessageStateResponse;
import com.decade.practice.inbox.dto.mapper.MessageMapper;
import com.decade.practice.inbox.utils.LogUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messages;
    private final MessageMapper messageMapper;

    @Override
    @ReadPolicy
    public List<MessageStateResponse> findByChatAndSequenceLessThanEqual(String chatId, UUID owner, Long anchorSequenceNumber) {

        if (anchorSequenceNumber == null) {
            anchorSequenceNumber = Long.MAX_VALUE;
        }

        List<Message> messageList = messages.findByChatIdAndSequenceIdLessThanEqual(chatId, anchorSequenceNumber, LogUtils.SEQUENCE_DESC_PAGE);

        return messageList.stream().map(Message::getState).map(messageMapper::map).toList();
    }
}
