package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.InboxLogResponse;

import java.util.List;
import java.util.UUID;

public interface LogService {

      List<InboxLogResponse> findBySequenceGreaterThanEqual(
                UUID owner,
                Long anchorSequenceNumber
      );

      List<InboxLogResponse> findByChatAndSequenceGreaterThanEqual(
                String chatId,
                UUID owner,
                Long anchorSequenceNumber
      );


}
