package com.decade.practice.live;

import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor
class RealtimeMessageTest extends BaseTestClass {

    @LocalServerPort
    int port = 0;
    @Value("${websocket.topics.queue}")
    String queueTopic;

    @Value("${websocket.topics.user}")
    String userTopic;

    @Value("${broker.topics.queue}")
    String brokerQueueTopic;

    @Autowired
    final MessageConverter converter;

    @Autowired
    final TokenService tokenService;


    final RedisTemplate<String, Object> redisTemplate;

    record SomeDTO(String someField) {
    }

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
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void giveAliceAndBobOnline_whenAliceSendToBob_thenBobReceiveMessageViaWebsocket() throws Exception {

        StompSession aliceSession = null;
        StompSession bobSession = null;
        WebSocketStompClient stompClient = null;

        try {
            UserClaims alice = new UserClaims(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "alice",
                "alice",
                "ROLE_USER"
            );


            UserClaims bob = new UserClaims(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "bob",
                "bob",
                "ROLE_USER"
            );

            String aliceToken = tokenService.encodeToken(alice, Duration.ofDays(5));
            String bobToken = tokenService.encodeToken(bob, Duration.ofDays(5));

            CompletableFuture<SomeDTO> aliceEvent = new CompletableFuture<>();
            CompletableFuture<SomeDTO> bobEvent = new CompletableFuture<>();

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

            Assertions.assertNotNull(aliceSession);
            Assertions.assertNotNull(bobSession);


            aliceSession.subscribe(userTopic + queueTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return SomeDTO.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((SomeDTO) payload);
                }
            });

            bobSession.subscribe(userTopic + queueTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return SomeDTO.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    bobEvent.complete((SomeDTO) payload);
                }
            });
            Thread.sleep(5000);
            redisTemplate.convertAndSend(brokerQueueTopic + ":" + alice.id(), new SomeDTO("Hello"));
            redisTemplate.convertAndSend(brokerQueueTopic + ":" + bob.id(), new SomeDTO("How are you"));

            assertThat(aliceEvent.get(10, TimeUnit.SECONDS)).extracting(SomeDTO::someField)
                .isEqualTo("Hello");

            assertThat(bobEvent.get(10, TimeUnit.SECONDS)).extracting(SomeDTO::someField)
                .isEqualTo("How are you");

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