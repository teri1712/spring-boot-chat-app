package com.decade.practice.inbox.integration;

import com.decade.practice.engagement.domain.events.ParticipantAdded;
import com.decade.practice.inbox.application.events.ConversationManagement;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.Conversation;
import com.decade.practice.inbox.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ConversationManagementTest extends ModuleBatchHandlingTest {

    final ConversationManagement conversationManagement;
    final RoomRepository rooms;
    final ConversationRepository conversations;

    public ConversationManagementTest(ConversationRepository conversations, LogRepository logs, RoomRepository rooms, ConversationManagement conversationManagement, RoomRepository rooms1, ConversationRepository conversations1) {
        super(conversations, logs, rooms);
        this.conversationManagement = conversationManagement;
        this.rooms = rooms1;
        this.conversations = conversations1;
    }


    @Test
    void givenGroupExist_whenAddParticipant_thenConversationOfTheParticipantIsCreatedAndNumberOfParticipantsOfTheRoomIsUpdated(Scenario scenario) {
        // given
        String chatId = "12345678-1234-1234-1234-123456789012";
        UUID participant1 = UUID.randomUUID();
        UUID participant2 = UUID.randomUUID();
        UUID participant3 = UUID.randomUUID();
        UUID participant4 = UUID.randomUUID();
        UUID callerId = participant1;
        conversationManagement.create(chatId, callerId, Set.of(participant1, participant2), "hello");

        scenario
            .publish(new ParticipantAdded(chatId, Set.of(participant4, participant3), Instant.now()))
            .andWaitForStateChange(new Supplier<List<ConversationView>>() {
                @Override
                public List<ConversationView> get() {
                    Room room = rooms.findByChatId(chatId).orElseThrow();
                    if (room.getParticipantCount() != 4) {
                        return null;
                    }
                    return conversations.findByChatIdBetweenParticipantIndex(chatId, 0, 5);
                }
            })
            .andVerify(convos -> {
                assertThat(convos)
                    .hasSize(4)
                    .extracting(ConversationView::conversation)
                    .extracting(Conversation::getOwnerId)
                    .contains(participant1, participant2, participant3, participant4);
            });

    }

}
