package com.decade.practice.live.integration;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.live.dto.TypeMessage;
import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.decade.practice.shared.security.TokenUtils.BEARER;
import static com.decade.practice.shared.security.TokenUtils.HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@RequiredArgsConstructor
class RoomJoinerTest extends BaseLiveTest {

    @LocalServerPort
    int port = 0;

    @Value("${websocket.topics.room}")
    String roomTopic;

    @Autowired
    final MessageConverter converter;

    @Autowired
    final TokenService tokenService;

    @MockitoSpyBean
    EngagementApi engagementApi;

    @BeforeEach
    void allowEngagement() {
        when(engagementApi.canRead(any(), any()))
            .thenReturn(true);

        when(engagementApi.canWrite(any(), any()))
            .thenReturn(true);
    }

    @Test
    @Timeout(20)
    @Sql(value = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_chats.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void givenAliceAndBobIsOnline_whenAliceTypeSth_thenBobReceiveTheTypeEvent() throws Exception {


        StompSession aliceSession = null;
        StompSession bobSession = null;
        WebSocketStompClient stompClient = null;

        try {

            UserClaims alice = new UserClaims(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "alice",
                "alice",
                "vcl.jpg");

            UserClaims bob = new UserClaims(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "bob",
                "bob",
                "vcl.jpg");

            String chatId = "12345678";

            String aliceToken = tokenService.encodeToken(alice, Duration.ofDays(5));
            String bobToken = tokenService.encodeToken(bob, Duration.ofDays(5));

            CompletableFuture<TypeMessage> aliceEvent = new CompletableFuture<>();
            CompletableFuture<TypeMessage> bobEvent = new CompletableFuture<>();

            stompClient = new WebSocketStompClient(new StandardWebSocketClient());
            stompClient.setMessageConverter(converter);

            StompHeaders aliceHeaders = new StompHeaders();
            aliceHeaders.add(HEADER_NAME, BEARER + aliceToken);
            aliceSession = stompClient.connectAsync(
                "ws://localhost:" + port + "/handshake",
                new WebSocketHttpHeaders(),
                aliceHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        log.error("Error", exception);
                    }
                }).get(10, TimeUnit.SECONDS);

            StompHeaders bobHeaders = new StompHeaders();
            bobHeaders.add(HEADER_NAME, BEARER + bobToken);
            bobSession = stompClient.connectAsync(
                "ws://localhost:" + port + "/handshake",
                new WebSocketHttpHeaders(),
                bobHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        log.error("Error", exception);
                    }
                }).get(10, TimeUnit.SECONDS);


            StompHeaders aliceStompHeaders = new StompHeaders();
            aliceStompHeaders.setDestination(roomTopic + "/" + chatId);

            StompHeaders bobStompHeaders = new StompHeaders();
            bobStompHeaders.setDestination(roomTopic + "/" + chatId);

            aliceSession.subscribe(aliceStompHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TypeMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((TypeMessage) payload);
                }
            });

            bobSession.subscribe(bobStompHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TypeMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    bobEvent.complete((TypeMessage) payload);
                }
            });

            StompHeaders chatStompHeaders = new StompHeaders();
            chatStompHeaders.setDestination(roomTopic + "/" + chatId);
            bobSession.send(chatStompHeaders, "Hello");

            TypeMessage aliceMessage = aliceEvent.get(5, TimeUnit.SECONDS);
            TypeMessage bobMessage = bobEvent.get(5, TimeUnit.SECONDS);
            assertThat(aliceMessage.from())
                .isEqualTo(bobMessage.from());


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