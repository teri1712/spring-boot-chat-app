package com.decade.practice.ws;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.core.ChatOperations;
import com.decade.practice.core.UserOperations;
import com.decade.practice.model.domain.TypeEvent;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.security.jwt.JwtCredentialService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static com.decade.practice.utils.TokenUtils.BEARER;
import static com.decade.practice.utils.TokenUtils.HEADER_NAME;
import static com.decade.practice.websocket.arguments.ChatIdentifierArgumentResolver.CHAT_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
      webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TypeEventTest {

      @LocalServerPort
      private int port = 0;

      @Autowired
      private MessageConverter converter;

      @Autowired
      private UserOperations userOperations;

      @Autowired
      private ChatOperations chatOperations;

      @Autowired
      private JwtCredentialService credentialService;

      private User me;
      private User you;
      private Chat chat;
      private String myToken;
      private String yourToken;
      private StompSession mySession;
      private StompSession yourSession;

      @BeforeAll
      public void beforeAll() {
            me = userOperations.create("first", "first", "first", new Date(), "male", null, true);
            you = userOperations.create("second", "second", "second", new Date(), "male", null, true);
            chat = chatOperations.getOrCreateChat(ChatIdentifier.from(me, you));

            myToken = credentialService.create(me, null).getAccessToken();
            yourToken = credentialService.create(you, null).getAccessToken();
      }

      @BeforeEach
      public void setUp() throws Exception {
            WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
            stompClient.setMessageConverter(converter);

            WebSocketHttpHeaders myHeaders = new WebSocketHttpHeaders();
            myHeaders.add(HEADER_NAME, BEARER + myToken);
            mySession = stompClient.connectAsync(
                  "ws://localhost:" + port + "/handshake",
                  myHeaders,
                  new StompSessionHandlerAdapter() {
                  }).get();

            WebSocketHttpHeaders yourHeaders = new WebSocketHttpHeaders();
            yourHeaders.add(HEADER_NAME, BEARER + yourToken);
            yourSession = stompClient.connectAsync(
                  "ws://localhost:" + port + "/handshake",
                  yourHeaders,
                  new StompSessionHandlerAdapter() {
                  }).get();
      }

      @AfterEach
      public void cleanUp() {
            mySession.disconnect();
            yourSession.disconnect();
      }

      @Test
      @Order(2)
      public void Type_Expect_Both_Receive_Event() throws Exception {
            CompletableFuture<TypeEvent> myEvent = new CompletableFuture<>();
            CompletableFuture<TypeEvent> yourEvent = new CompletableFuture<>();

            StompHeaders myHeaders = new StompHeaders();
            myHeaders.setDestination("/typing");
            myHeaders.set(CHAT_HEADER, chat.getIdentifier().toString());

            StompHeaders yourHeaders = new StompHeaders();
            yourHeaders.setDestination("/typing");
            yourHeaders.set(CHAT_HEADER, chat.getIdentifier().toString());

            mySession.subscribe(myHeaders, new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                        return TypeEvent.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload == null)
                              return;
                        myEvent.complete((TypeEvent) payload);
                  }
            });

            yourSession.subscribe(yourHeaders, new StompFrameHandler() {
                  @Override
                  public Type getPayloadType(StompHeaders headers) {
                        return TypeEvent.class;
                  }

                  @Override
                  public void handleFrame(StompHeaders headers, Object payload) {
                        if (payload == null)
                              return;
                        yourEvent.complete((TypeEvent) payload);
                  }
            });
            Thread.sleep(200);

            StompHeaders h = new StompHeaders();
            h.set(CHAT_HEADER, chat.getIdentifier().toString());
            h.setDestination("/typing");
            yourSession.send(h, "Hello");

            assertNotNull(myEvent.get());
            assertNotNull(yourEvent.get());
            assertEquals(yourEvent.get(), myEvent.get());
      }

      @Autowired
      private RedisTemplate<Object, Object> redisTemplate;

      @AfterAll
      public void tearDown() {
            redisTemplate.execute((RedisConnection conn) -> {
                  conn.flushDb();
                  return null;
            });
      }
}
