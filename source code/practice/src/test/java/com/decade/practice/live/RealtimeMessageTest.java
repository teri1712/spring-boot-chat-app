package com.decade.practice.live;

import com.decade.practice.BaseTestClass;
import com.decade.practice.TestBeans;
import com.decade.practice.chat.application.ports.in.ChatService;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.dto.InboxLogResponse;
import com.decade.practice.inbox.dto.TextStateResponse;
import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.*;
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

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RealtimeMessageTest extends BaseTestClass {

      @LocalServerPort
      private int port = 0;

      @Autowired
      private MessageConverter converter;

      @Autowired
      private TokenService tokenService;

      @Value("${websocket.topics.queue}")
      private String queueTopic;

      @Value("${websocket.topics.user}")
      private String userTopic;


      @Autowired
      private TestBeans.PrivateChatSender chatSender;

      @Autowired
      private ApplicationEvents events;

      @Autowired
      private ChatService chatService;

      @Test
      @Timeout(value = 10, unit = TimeUnit.SECONDS)
      @Sql(value = {"/sql/clean.sql", "/sql/seed_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
      public void giveAliceAndBobOnline_whenAliceSendToBob_thenBobReceiveMessageViaWebsocket() throws Exception {

            StompSession aliceSession = null;
            StompSession bobSession = null;
            WebSocketStompClient stompClient = null;

            try {

                  UUID aliceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
                  UUID bobId = UUID.fromString("22222222-2222-2222-2222-222222222222");

                  chatService.getDirect(aliceId, bobId);

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

                  CompletableFuture<InboxLogResponse> aliceEvent = new CompletableFuture<>();
                  CompletableFuture<InboxLogResponse> bobEvent = new CompletableFuture<>();

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
                            }).get(5, TimeUnit.SECONDS);

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
                            }).get(5, TimeUnit.SECONDS);

                  Assertions.assertNotNull(aliceSession);
                  Assertions.assertNotNull(bobSession);


                  aliceSession.subscribe(userTopic + queueTopic, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                              return InboxLogResponse.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                              aliceEvent.complete((InboxLogResponse) payload);
                        }
                  });

                  bobSession.subscribe(userTopic + queueTopic, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                              return InboxLogResponse.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                              bobEvent.complete((InboxLogResponse) payload);
                        }
                  });

                  Thread.sleep(1000);

                  chatSender.sendPrivateText("Hello Bob", alice.id(), bob.id());
                  assertThat(events.stream(MessageCreated.class)).hasSize(1);
                  assertThat(events.stream(InboxLogCreated.class)).hasSize(2);
                  Assertions.assertNotNull(aliceEvent.get(10, TimeUnit.SECONDS));
                  Assertions.assertNotNull(bobEvent.get(10, TimeUnit.SECONDS));
                  Assertions.assertNotNull(((TextStateResponse) aliceEvent.get(2, TimeUnit.SECONDS).messageState()).getContent());
                  Assertions.assertNotNull(((TextStateResponse) bobEvent.get(2, TimeUnit.SECONDS).messageState()).getContent());
                  Assertions.assertEquals("TEXT", aliceEvent.get(2, TimeUnit.SECONDS).messageState().getMessageType());
                  Assertions.assertEquals(aliceEvent.get(2, TimeUnit.SECONDS).messageState().getChatId(), bobEvent.get(2, TimeUnit.SECONDS).messageState().getChatId());
                  Assertions.assertEquals(((TextStateResponse) aliceEvent.get(2, TimeUnit.SECONDS).messageState()).getContent(), ((TextStateResponse) bobEvent.get(2, TimeUnit.SECONDS).messageState()).getContent());
                  Assertions.assertEquals("Hello Bob", ((TextStateResponse) aliceEvent.get(2, TimeUnit.SECONDS).messageState()).getContent());
                  Assertions.assertEquals(alice.id(), aliceEvent.get(2, TimeUnit.SECONDS).messageState().getSender().id());

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