package com.decade.practice.engagement.integration;

import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.Participant;
import com.decade.practice.integration.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EngagementControllerTest extends BaseTestClass {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ChatRepository chats;

    @Autowired
    ParticipantRepository participants;

    @Test
    @WithUserDetails("alice")
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenGroupExistWithAliceAndBob_whenAliceAddCharlie_thenReturnParticipantsIncludes3OfThem() throws Exception {
        UUID alice = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bob = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlie = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String chatId = UUID.randomUUID().toString();
        Chat chat = new Chat(chatId, 1000, new ChatCreators(alice, Set.of(bob)));
        chats.save(chat);

        participants.saveAll(Set.of(new Participant(alice, chatId), new Participant(bob, chatId)));

        mockMvc.perform(post("/engagements/{id}/participants", chatId)
                .param("partner", charlie.toString())
            )
            .andExpect(status().isCreated());

        assertThat(participants.findByChatId(chatId))
            .hasSize(3)
            .contains(alice, bob, charlie);
    }

    @Test
    @WithUserDetails("alice")
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenGroupExistWithAliceAndBobAndCapacityIs2_whenAliceAddCharlie_thenMustReturnBadRequest() throws Exception {
        UUID alice = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bob = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlie = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String chatId = UUID.randomUUID().toString();
        Chat chat = new Chat(chatId, 2, new ChatCreators(alice, Set.of(bob)));
        chats.save(chat);

        participants.saveAll(Set.of(new Participant(alice, chatId), new Participant(bob, chatId)));

        mockMvc.perform(post("/engagements/{id}/participants", chatId)
                .param("partner", charlie.toString())
            )
            .andExpect(status().isBadRequest());

        assertThat(participants.findByChatId(chatId))
            .hasSize(2)
            .contains(alice, bob);
    }

}
