package com.decade.practice.inbox.application.query;

import com.decade.practice.inbox.dto.InboxLogResponse;

import java.util.List;
import java.util.UUID;

public interface LogService {

      List<InboxLogResponse> findBySequenceLessThanEqual(
                UUID owner,
                Long anchorSequenceId
      );

      List<InboxLogResponse> findByChatAndSequenceLessThanEqual(
                UUID owner,
                String chatId,
                Long anchorSequenceId
      );


}
