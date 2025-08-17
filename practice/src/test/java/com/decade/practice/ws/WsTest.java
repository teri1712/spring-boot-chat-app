package com.decade.practice.ws;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.TextEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.domain.entity.WelcomeEvent;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.usecases.UserOperations;
import com.decade.practice.utils.PrerequisiteBeans;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.decade.practice.utils.TokenUtils.BEARER;
import static com.decade.practice.utils.TokenUtils.HEADER_NAME;
import static com.decade.practice.websocket.WebSocketConfiguration.HANDSHAKE_DESTINATION;
import static com.decade.practice.websocket.WebSocketConfiguration.USER_QUEUE_DESTINATION;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WsTest {


        @LocalServerPort
        private int port = 0;

        @Autowired
        private MessageConverter converter;

        @Autowired
        private UserOperations userOperations;

        @Autowired
        private JwtCredentialService credentialService;

        private User me;
        private User you;
        private String myToken;
        private String yourToken;
        private RestClient myClient;
        private RestClient yourClient;
        private StompSession mySession;
        private StompSession yourSession;

        @BeforeAll
        public void beforeAll() {
                me = userOperations.create("first", "first", "first", new Date(), "male", null, true);
                you = userOperations.create("second", "second", "second", new Date(), "male", null, true);

                myToken = credentialService.create(me, null).getAccessToken();
                yourToken = credentialService.create(you, null).getAccessToken();

                myClient = RestClient.builder()
                        .baseUrl("http://localhost:" + port + "/events")
                        .defaultHeader(HEADER_NAME, BEARER + myToken)
                        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .build();

                yourClient = RestClient.builder()
                        .baseUrl("http://localhost:" + port + "/events")
                        .defaultHeader(HEADER_NAME, BEARER + yourToken)
                        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .build();
        }

        @BeforeEach
        public void setUp() throws Exception {
                WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
                stompClient.setMessageConverter(converter);

                WebSocketHttpHeaders myHeaders = new WebSocketHttpHeaders();
                myHeaders.add(HEADER_NAME, BEARER + myToken);
                mySession = stompClient.connectAsync(
                        "ws://localhost:" + port + HANDSHAKE_DESTINATION,
                        myHeaders,
                        new StompSessionHandlerAdapter() {
                        }).get();

                WebSocketHttpHeaders yourHeaders = new WebSocketHttpHeaders();
                yourHeaders.add(HEADER_NAME, BEARER + yourToken);
                yourSession = stompClient.connectAsync(
                        "ws://localhost:" + port + HANDSHAKE_DESTINATION,
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
        @Timeout(value = 3, unit = TimeUnit.SECONDS)
        @Order(1)
        public void testSubscribeReceivesWelcomeEvent() throws Exception {
                CompletableFuture<WelcomeEvent> myEvent = new CompletableFuture<>();
                CompletableFuture<WelcomeEvent> yourEvent = new CompletableFuture<>();

                mySession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                                return WelcomeEvent.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                                myEvent.complete((WelcomeEvent) payload);
                        }
                });

                yourSession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                                return WelcomeEvent.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                                yourEvent.complete((WelcomeEvent) payload);
                        }
                });

                assertNotNull(yourEvent.get());
                assertNotNull(myEvent.get());
        }

        @Test
        @Timeout(value = 3, unit = TimeUnit.SECONDS)
        @Order(2)
        public void testPostMessageIsReceivedByBothUsers() throws Exception {
                CompletableFuture<WelcomeEvent> myEcho = new CompletableFuture<>();
                CompletableFuture<WelcomeEvent> yourEcho = new CompletableFuture<>();
                CompletableFuture<TextEvent> myEvent = new CompletableFuture<>();
                CompletableFuture<TextEvent> yourEvent = new CompletableFuture<>();

                mySession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                                if (myEcho.isDone())
                                        return TextEvent.class;
                                return WelcomeEvent.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                                if (!myEcho.isDone()) {
                                        myEcho.complete((WelcomeEvent) payload);
                                } else {
                                        myEvent.complete((TextEvent) payload);
                                }
                        }
                });

                yourSession.subscribe(USER_QUEUE_DESTINATION, new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                                if (yourEcho.isDone())
                                        return TextEvent.class;
                                return WelcomeEvent.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                                if (!yourEcho.isDone()) {
                                        yourEcho.complete((WelcomeEvent) payload);
                                } else {
                                        yourEvent.complete((TextEvent) payload);
                                }
                        }
                });

                TextEvent event = new TextEvent(new Chat(me, you), me, "Hello how are you");

                TextEvent received = myClient.post()
                        .contentType(MediaType.APPLICATION_JSON).body(event)
                        .retrieve().body(TextEvent.class);

                Assertions.assertNotNull(received);
                Assertions.assertNotNull(myEcho.get().getContent());
                Assertions.assertNotNull(yourEcho.get().getContent());

                Assertions.assertEquals(myEvent.get().getChatIdentifier(), yourEvent.get().getChatIdentifier());
                Assertions.assertEquals("TEXT", myEvent.get().getEventType());

                Assertions.assertEquals(((TextEvent) myEvent.get()).getContent(), ((TextEvent) yourEvent.get()).getContent());
                Assertions.assertEquals("Hello how are you", ((TextEvent) myEvent.get()).getContent());
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