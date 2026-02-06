package com.decade.practice.cache;

import com.decade.practice.common.BaseTestClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestPropertySource(properties = {
        "server.cache.events=true"
})
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EventCacheTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void test1() throws Exception {
//        List<EventResponse> vcl = objectMapper.readValue("[{\"id\":\"00000000-0000-0000-0001-000000000044\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000044\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"I told you\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:55:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":22,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000043\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000043\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Delicious!\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:55:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":21,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000040\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000040\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Quickly\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:54:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":20,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000039\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000039\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"I'm here\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:54:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":19,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000036\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000036\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Run!\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:53:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":18,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000035\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000035\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"On my way\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:53:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":17,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000032\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000032\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Coming?\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:52:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":16,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000031\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000031\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Yummm\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:52:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":15,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000028\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000028\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"It's curry\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:51:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":14,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000027\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000027\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Is it soup?\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:51:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":13,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000024\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000024\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"He knows already\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:50:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":12,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000023\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000023\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Tell him I'm hungry\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:50:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":11,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000020\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000020\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Sanji is there\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:49:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":10,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000019\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000019\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Let's go to the kitchen\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:49:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":9,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000016\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000016\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Cotton Candy!\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:48:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":8,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000015\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000015\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Meat?\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:48:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":7,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000012\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000012\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Always!\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:47:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":6,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000011\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000011\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Are you hungry?\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:47:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":5,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000008\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000008\",\"sender\":\"00000000-0000-0000-0000-000000000004\",\"textEvent\":{\"content\":\"Hi Luffy\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:46:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":4,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}},{\"id\":\"00000000-0000-0000-0001-000000000007\",\"idempotencyKey\":\"00000000-0000-0000-0002-000000000007\",\"sender\":\"00000000-0000-0000-0000-000000000002\",\"textEvent\":{\"content\":\"Hi Chopper\"},\"imageEvent\":null,\"iconEvent\":null,\"preferenceEvent\":null,\"fileEvent\":null,\"seenEvent\":null,\"createdTime\":\"2026-02-06T18:46:19.518519Z\",\"eventType\":\"TEXT\",\"eventVersion\":3,\"message\":true,\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\",\"chat\":{\"identifier\":{\"firstUser\":\"00000000-0000-0000-0000-000000000002\",\"secondUser\":\"00000000-0000-0000-0000-000000000004\"},\"owner\":\"00000000-0000-0000-0000-000000000002\",\"partner\":\"00000000-0000-0000-0000-000000000004\"}}]", new TypeReference<List<EventResponse>>() {
//        });
//        Assertions.assertNotEquals(vcl.get(0).id().toString(), "00000000-0000-0000-0001-000000000044");
//        log.error("vcl123" + vcl.toString());
//    }


    @Test
    @Sql({"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql", "/sql/seed_events.sql"})
    @WithUserDetails("alice")
    public void givenAliceHasEvents_whenAliceQueryListing_thenReturnEventsAndAreCached() throws Exception {
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

        log.trace("Calling get events for Chat Id: {}", chatId);
        Instant start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        Instant end = Instant.now();
        log.trace("Fetched events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

        log.trace("Calling get events after events already cached for Chat Id: {}", chatId);
        start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        end = Instant.now();
        log.trace("Fetched cached events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

    }

    @Test
    @Sql({"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql", "/sql/seed_events.sql"})
    @WithUserDetails("alice")
    public void givenEventsOnCached_whenAliceSendNewMessage_thenReturnEventsAreNotInCached() throws Exception {
        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

        mockMvc.perform(post("/chats/{chatIdentifier}/text-events", chatId)
                        .header("Idempotency-key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"To Bob\" }"))
                .andExpect(status().isCreated());

        Instant start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$[0].textEvent.content").value("To Bob"));

        Instant end = Instant.now();
        log.trace("Fetched events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

        log.trace("Calling get events after events already cached for Chat Id: {}", chatId);
        start = Instant.now();
        mockMvc.perform(get("/chats/{chatId}/events", chatId)
                        .param("atVersion", "102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20));
        end = Instant.now();
        log.trace("Fetched cached events for Chat Id: {} with response time: {}", chatId, Duration.between(start, end).toMillis());

    }


}
