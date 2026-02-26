package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.MessageStateResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {

      List<MessageStateResponse> findByChatAndSequenceLessThanEqual(
                UUID owner,
                String chatId,
                Long anchorSequenceId
      );

}
