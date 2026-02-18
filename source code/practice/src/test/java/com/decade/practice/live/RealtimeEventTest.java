package com.decade.practice.live;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.threads.domain.events.EventCreated;
import com.decade.practice.threads.domain.events.EventReady;
import com.decade.practice.threads.dto.TextResponse;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.decade.practice.web.security.TokenUtils.BEARER;
import static com.decade.practice.web.security.TokenUtils.HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RealtimeEventTest extends BaseTestClass {

    @LocalServerPort
    private int port = 0;

    @Autowired
    private MessageConverter converter;

    @Autowired
    private TokenService tokenService;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    @Value("${websocket.topics.queue}")
    private String userTopic;


    @Autowired
    private TestBeans.PrivateChatSender chatSender;

    @Autowired
    private ApplicationEvents events;

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @Sql(value = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void giveAliceAndBobOnline_whenAliceSendToBob_thenBobReceiveMessageViaWebsocket() throws Exception {

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

            String aliceToken = tokenService.encodeToken(alice, Duration.ofDays(5).toMillis());
            String bobToken = tokenService.encodeToken(bob, Duration.ofDays(5).toMillis());

            CompletableFuture<TextResponse> aliceEvent = new CompletableFuture<>();
            CompletableFuture<TextResponse> bobEvent = new CompletableFuture<>();

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


            aliceSession.subscribe(userTopic + "/" + queueTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TextResponse.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    aliceEvent.complete((TextResponse) payload);
                }
            });

            bobSession.subscribe(userTopic + "/" + queueTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TextResponse.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    bobEvent.complete((TextResponse) payload);
                }
            });

            chatSender.sendPrivateText("Hello Bob", alice.id(), bob.id());
            assertThat(events.stream(EventCreated.class)).hasSize(2);
            assertThat(events.stream(EventReady.class)).hasSize(2);
            Assertions.assertNotNull(aliceEvent.get(2, TimeUnit.SECONDS));
            Assertions.assertNotNull(bobEvent.get(2, TimeUnit.SECONDS));
            Assertions.assertNotNull(aliceEvent.get().getContent());
            Assertions.assertNotNull(bobEvent.get().getContent());
            Assertions.assertEquals("TEXT", aliceEvent.get().getEventType());
            Assertions.assertEquals(aliceEvent.get().getChatId(), bobEvent.get().getChatId());

            Assertions.assertEquals(aliceEvent.get().getContent(), bobEvent.get().getContent());
            Assertions.assertEquals("Hello Bob", aliceEvent.get().getContent());
            Assertions.assertEquals(alice.id(), aliceEvent.get().getSenderId());

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