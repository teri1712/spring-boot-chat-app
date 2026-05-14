package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("kafka-batch-handling")
class KafkaBatchHandlingTest extends BatchHandlingTest {

    public KafkaBatchHandlingTest(ConversationRepository conversations, LogRepository logs, RoomRepository rooms) {
        super(conversations, logs, rooms);
    }
}
