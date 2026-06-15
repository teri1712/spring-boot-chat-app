package com.decade.practice.engagement.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
@ComponentTest(datasets = {EngagementDataset.class})
@RequiredArgsConstructor
class EngagementControllerTest {
    final MockMvc mockMvc;
    final EngagementApi engagementApi;
    final ParticipantRepository participants;

    @Test
    void givenGroupExistWithAliceAndBob_whenAliceAddCharlie_thenReturnParticipantsIncludes3OfThem() throws Exception {
        UUID alice = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bob = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlie = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String chatId = engagementApi.createGroup(alice, Set.of(bob)).identifier();

        mockMvc.perform(post("/engagements/{id}/participants", chatId)
                .param("partner", charlie.toString())
            )
            .andExpect(status().isCreated());

        assertThat(participants.findByChatId(chatId))
            .hasSize(3)
            .contains(alice, bob, charlie);
    }

    @Test
    void givenGroupExistWithAliceAndBobAndCapacityIs2_whenAliceAddCharlie_thenMustReturnBadRequest() throws Exception {
        UUID alice = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID bob = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID charlie = UUID.fromString("33333333-3333-3333-3333-333333333333");

        String chatId = engagementApi.createDirect(alice, bob).mapping().chatId();
        mockMvc.perform(post("/engagements/{id}/participants", chatId)
                .param("partner", charlie.toString())
            )
            .andExpect(status().isBadRequest());

        assertThat(participants.findByChatId(chatId))
            .hasSize(2)
            .contains(alice, bob);
    }

}
