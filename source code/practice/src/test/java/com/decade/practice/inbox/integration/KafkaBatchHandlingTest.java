package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(
    properties = "batch.mode=kafka"
)
class KafkaBatchHandlingTest extends BatchHandlingTest {

    public KafkaBatchHandlingTest(ConversationRepository conversations, LogRepository logs, RoomRepository rooms) {
        super(conversations, logs, rooms);
    }
}
