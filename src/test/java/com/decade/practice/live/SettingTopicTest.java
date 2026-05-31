package com.decade.practice.live;

import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;
import com.decade.practice.engagement.api.EngagementApi;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor
class SettingTopicTest extends BaseTestClass {

    @LocalServerPort
    int port = 0;

    @Value("${websocket.topics.setting}")
    String settingTopic;

    @Value("${broker.topics.setting}")
    String brokerSettingTopic;

    @MockitoSpyBean
    EngagementApi engagementApi;

    final MessageConverter converter;
    final TokenService tokenService;
    final RedisTemplate<String, Object> redisTemplate;

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
    public void givenAliceSubToSetting_whenBrokerEmitPrefMessage_thenAliceReceiveThePrefMessage() throws Exception {


        StompSession aliceSession = null;
        WebSocketStompClient stompClient = null;

        try {

            UserClaims alice = new UserClaims(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "alice",
                "alice",
                "vcl.jpg");


            String chatId = "12345678";

            String aliceToken = tokenService.encodeToken(alice, Duration.ofDays(5));

            CompletableFuture<PreferenceMessage> aliceEvent = new CompletableFuture<>();

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

            StompHeaders aliceStompHeaders = new StompHeaders();
            aliceStompHeaders.setDestination(settingTopic + "/" + chatId);

            aliceSession.subscribe(aliceStompHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return PreferenceMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((PreferenceMessage) payload);
                }
            });
            Thread.sleep(2000);

            redisTemplate.convertAndSend(brokerSettingTopic + ":" + chatId,
                PreferenceMessage.builder()
                    .iconId(1)
                    .customName("hello")
                    .build()
            );

            PreferenceMessage aliceMessage = aliceEvent.get(5, TimeUnit.SECONDS);
            assertThat(aliceMessage)
                .extracting(PreferenceMessage::getCustomName)
                .isEqualTo("hello");


        } finally {

            if (aliceSession != null && aliceSession.isConnected()) {
                aliceSession.disconnect();
            }
            if (stompClient != null) {
                stompClient.stop();
            }
        }

    }


}