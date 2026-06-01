package com.decade.practice.inbox.integration;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.integration.BaseTestClass;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.modulith.test.Scenario;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
abstract class BatchHandlingTest extends BaseTestClass {
    final ConversationRepository conversations;
    final LogRepository logs;
    final RoomRepository rooms;


    void publishAndAssertLogs(Object event,
                              UUID sender,
                              int expectedSize,
                              LogAction expectedAction,
                              Scenario scenario) {

        scenario.publish(event)
            .andWaitForStateChange(new Supplier<List<InboxLog>>() {
                @Override
                public List<InboxLog> get() {
                    var v = logs.findBySenderId(sender);
                    if (v.isEmpty())
                        return null;
                    return v;
                }
            })
            .andVerify(l -> {
                assertThat(l)
                    .hasSize(expectedSize)
                    .extracting(InboxLog::getMessageId)
                    .allMatch(id -> id == 1L)
                ;
                assertThat(l)
                    .extracting(InboxLog::getAction)
                    .allMatch(action -> action == expectedAction);

                assertThat(l)
                    .extracting(InboxLog::getOwnerId)
                    .containsAll(participants);
            });
    }

    String chatId;
    Room room;
    UUID sender;
    Set<UUID> participants;

    void setUp(int offset, int limit) {
        sender = UUID.randomUUID();
        chatId = "12345678-1234-1234-1234-123456789012";
        room = new Room("12345678-1234-1234-1234-123456789012", UUID.randomUUID(), null, null, new HashSet<>());
        rooms.save(room);
        participants = new HashSet<>();

        AtomicInteger index = new AtomicInteger();
        for (int i = 0; i < limit; i++) participants.add(UUID.randomUUID());
    
        conversations.saveAll(participants.stream()
            .map(participantId -> new Conversation(participantId, room.getId(), offset + index.getAndIncrement()))
            .toList());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 20, 40, 60, 80})
    void shouldBroadcastInsertLogToReceipientsInBatch(int batchOffset, Scenario scenario) {
        final int batchSize = 20;
        setUp(batchOffset, batchSize);
        MessageCreated messageCreated = new MessageCreated(
            1L,
            chatId,
            UUID.randomUUID(),
            sender,
            Instant.now(),
            "text",
            TextState.builder()
                .sequenceId(1L)
                .postingId(UUID.randomUUID())
                .senderId(sender)
                .messageType("TEXT")
                .chatId("chat-123")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .seenByIds(Set.of(
                    UUID.randomUUID(),
                    UUID.randomUUID()
                ))
                .content("Hello world")
                .build());
        var event = new BatchInsertionEvent(batchOffset, batchOffset + batchSize, messageCreated);
        publishAndAssertLogs(event, sender, batchSize, LogAction.ADDITION, scenario);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 20, 40, 60, 80})
    void shouldBroadcastUpdateLogToReceipientsInBatch(int batchOffset, Scenario scenario) {
        final int batchSize = 20;
        setUp(batchOffset, batchSize);
        MessageUpdated messageCreated = new MessageUpdated(
            1L,
            chatId,
            UUID.randomUUID(), sender,
            Instant.now(),
            TextState.builder()
                .sequenceId(1L)
                .postingId(UUID.randomUUID())
                .senderId(sender)
                .messageType("TEXT")
                .chatId("chat-123")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .seenByIds(Set.of(
                    UUID.randomUUID(),
                    UUID.randomUUID()
                ))
                .content("Hello world")
                .build());
        var event = new BatchUpdateEvent(batchOffset, batchOffset + batchSize, messageCreated);
        publishAndAssertLogs(event, sender, batchSize, LogAction.UPDATE, scenario);
    }
}
