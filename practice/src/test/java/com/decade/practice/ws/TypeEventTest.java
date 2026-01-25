package com.decade.practice.ws;

import com.decade.practice.api.web.converters.ChatIdentifierConverter;
import com.decade.practice.common.BaseTestClass;
import com.decade.practice.infra.security.UserClaimsTokenService;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.redis.TypeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.decade.practice.api.websocket.arguments.ChatIdentifierArgumentResolver.CHAT_HEADER;
import static com.decade.practice.utils.TokenUtils.BEARER;
import static com.decade.practice.utils.TokenUtils.HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TypeEventTest extends BaseTestClass {

    @LocalServerPort
    private int port = 0;

    @Autowired
    private MessageConverter converter;

    @Autowired
    private UserClaimsTokenService tokenService;

    @Test
    @Timeout(5)
    @Sql(value = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void givenAliceAndBobIsOnline_whenAliceTypeSth_thenBobReceiveTheTypeEvent() throws Exception {


        StompSession aliceSession = null;
        StompSession bobSession = null;
        WebSocketStompClient stompClient = null;

        try {

            UserClaims alice = UserClaims.builder()
                    .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                    .username("alice")
                    .role("ROLE_USER")
                    .gender(User.FEMALE)
                    .build();

            UserClaims bob = UserClaims.builder()
                    .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                    .username("bob")
                    .role("ROLE_USER")
                    .gender(User.MALE)
                    .build();

            String aliceToken = tokenService.create(alice, null).getAccessToken();
            String bobToken = tokenService.create(bob, null).getAccessToken();

            CompletableFuture<TypeEvent> aliceEvent = new CompletableFuture<>();
            CompletableFuture<TypeEvent> bobEvent = new CompletableFuture<>();

            stompClient = new WebSocketStompClient(new StandardWebSocketClient());
            stompClient.setMessageConverter(converter);


            WebSocketHttpHeaders aliceHttpHeaders = new WebSocketHttpHeaders();
            aliceHttpHeaders.add(HEADER_NAME, BEARER + aliceToken);
            aliceSession = stompClient.connectAsync(
                    "ws://localhost:" + port + "/handshake",
                    aliceHttpHeaders,
                    new StompSessionHandlerAdapter() {
                    }).get(2, TimeUnit.SECONDS);

            WebSocketHttpHeaders bobHttpHeaders = new WebSocketHttpHeaders();
            bobHttpHeaders.add(HEADER_NAME, BEARER + bobToken);
            bobSession = stompClient.connectAsync(
                    "ws://localhost:" + port + "/handshake",
                    bobHttpHeaders,
                    new StompSessionHandlerAdapter() {
                    }).get(2, TimeUnit.SECONDS);

            ChatIdentifier aliceBobChat = ChatIdentifier.from(UUID.fromString("11111111-1111-1111-1111-111111111111"), UUID.fromString("22222222-2222-2222-2222-222222222222"));

            StompHeaders aliceStompHeaders = new StompHeaders();
            aliceStompHeaders.setDestination("/typing");
            aliceStompHeaders.set(CHAT_HEADER, ChatIdentifierConverter.toString(aliceBobChat));

            StompHeaders bobStompHeaders = new StompHeaders();
            bobStompHeaders.setDestination("/typing");
            bobStompHeaders.set(CHAT_HEADER, ChatIdentifierConverter.toString(aliceBobChat));

            aliceSession.subscribe(aliceStompHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TypeEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((TypeEvent) payload);
                }
            });

            bobSession.subscribe(bobStompHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TypeEvent.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    bobEvent.complete((TypeEvent) payload);
                }
            });

            StompHeaders chatStompHeaders = new StompHeaders();
            chatStompHeaders.set(CHAT_HEADER, aliceBobChat.toString());
            chatStompHeaders.setDestination("/typing");
            bobSession.send(chatStompHeaders, "Hello");

            assertNotNull(aliceEvent.get(2, TimeUnit.SECONDS));
            assertNotNull(bobEvent.get(2, TimeUnit.SECONDS));
            assertEquals(bobEvent.get(2, TimeUnit.SECONDS), aliceEvent.get(2, TimeUnit.SECONDS));


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