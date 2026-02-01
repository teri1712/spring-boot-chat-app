package com.decade.practice.ws;

import com.decade.practice.common.BaseTestClass;
import com.decade.practice.dto.EventDto;
import com.decade.practice.infra.security.UserClaimsService;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.persistence.jpa.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.decade.practice.infra.configs.WebSocketConfiguration.USER_QUEUE_DESTINATION;
import static com.decade.practice.utils.TokenUtils.BEARER;
import static com.decade.practice.utils.TokenUtils.HEADER_NAME;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RealtimeEventTest extends BaseTestClass {

    @LocalServerPort
    private int port = 0;

    @Autowired
    private MessageConverter converter;

    @Autowired
    private UserClaimsService tokenService;


    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @Sql(value = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void giveAliceAndBobOnline_whenAliceSendToBob_thenBobReceiveMessageViaWebsocket() throws Exception {

        StompSession aliceSession = null;
        StompSession bobSession = null;
        WebSocketStompClient stompClient = null;

        try {
            UserClaims alice = UserClaims.builder()
                    .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    .username("alice")
                    .name("alice")
                    .role("ROLE_USER")
                    .gender(User.FEMALE)
                    .build();

            UserClaims bob = UserClaims.builder()
                    .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                    .username("bob")
                    .name("bob")
                    .role("ROLE_USER")
                    .gender(User.MALE)
                    .build();
            String aliceBobChat = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";

            String aliceToken = tokenService.create(alice, null).getAccessToken();
            String bobToken = tokenService.create(bob, null).getAccessToken();

            RestClient aliceClient = RestClient.builder()
                    .baseUrl("http://localhost:" + port + "/chats/" + aliceBobChat + "/text-events")
                    .defaultHeader(HEADER_NAME, BEARER + aliceToken)
                    .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .build();
            CompletableFuture<EventDto> aliceEvent = new CompletableFuture<>();
            CompletableFuture<EventDto> bobEvent = new CompletableFuture<>();

            stompClient = new WebSocketStompClient(new StandardWebSocketClient());
            stompClient.setMessageConverter(converter);


            StompHeaders aliceHeaders = new StompHeaders();
            aliceHeaders.add(HEADER_NAME, BEARER + aliceToken);
            aliceSession = stompClient.connectAsync(
                    "ws://localhost:" + port + "/handshake",
                    new WebSocketHttpHeaders(),
                    aliceHeaders,
                    new StompSessionHandlerAdapter() {
                    }).get(2, TimeUnit.SECONDS);

            StompHeaders bobHeaders = new StompHeaders();
            bobHeaders.add(HEADER_NAME, BEARER + bobToken);
            bobSession = stompClient.connectAsync(
                    "ws://localhost:" + port + "/handshake",
                    new WebSocketHttpHeaders(),
                    bobHeaders,
                    new StompSessionHandlerAdapter() {
                    }).get(2, TimeUnit.SECONDS);

            Assertions.assertNotNull(aliceSession);
            Assertions.assertNotNull(bobSession);


            aliceSession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return EventDto.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((EventDto) payload);
                }
            });

            bobSession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return EventDto.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    bobEvent.complete((EventDto) payload);
                }
            });
            String eventJson = """
                    {
                        "content": "Hello Bob"
                    }
                    """;

            EventDto received = aliceClient.post()
                    .header("Idempotency-key", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON).body(eventJson)
                    .retrieve().body(EventDto.class);

            Assertions.assertNotNull(received);

            Assertions.assertNotNull(aliceEvent.get(2, TimeUnit.SECONDS));
            Assertions.assertNotNull(bobEvent.get(2, TimeUnit.SECONDS));
            Assertions.assertNotNull(aliceEvent.get().getTextEvent());
            Assertions.assertNotNull(bobEvent.get().getTextEvent());
            Assertions.assertEquals("TEXT", aliceEvent.get().getEventType());
            Assertions.assertEquals(aliceEvent.get().getChat().getIdentifier(), bobEvent.get().getChat().getIdentifier());

            Assertions.assertEquals(aliceEvent.get().getTextEvent().getContent(), bobEvent.get().getTextEvent().getContent());
            Assertions.assertEquals("Hello Bob", aliceEvent.get().getTextEvent().getContent());
            Assertions.assertEquals(alice.getId(), aliceEvent.get().getSender());

        } finally {

            if (aliceSession != null && aliceSession.isConnected()) {
                aliceSession.disconnect();
            }
            if (bobSession != null && bobSession.isConnected()) {
                bobSession.disconnect();
            }
            if (stompClient != null) {
                stompClient.stop();
            }
        }


    }

}