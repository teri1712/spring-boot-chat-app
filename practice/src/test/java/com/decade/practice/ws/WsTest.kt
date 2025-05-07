package com.decade.practice.ws

import com.decade.practice.core.UserOperations
import com.decade.practice.database.transaction.create
import com.decade.practice.model.domain.entity.*
import com.decade.practice.security.jwt.JwtCredentialService
import com.decade.practice.utils.TokenUtils.BEARER
import com.decade.practice.utils.TokenUtils.HEADER_NAME
import com.decade.practice.websocket.HANDSHAKE_DESTINATION
import com.decade.practice.websocket.USER_QUEUE_DESTINATION
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.client.RestClient
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull


@SpringBootTest(
      webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
      properties = ["spring.jpa.database=H2"]
)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WsTest {

      @LocalServerPort
      val port = 0

      @Autowired
      lateinit var converter: MessageConverter

      @Autowired
      lateinit var userOperations: UserOperations

      @Autowired
      lateinit var credentialService: JwtCredentialService

      lateinit var me: User
      lateinit var you: User
      lateinit var myToken: String
      lateinit var yourToken: String
      lateinit var myClient: RestClient
      lateinit var yourClient: RestClient
      lateinit var mySession: StompSession
      lateinit var yourSession: StompSession

      @BeforeAll
      fun beforeAll() {
            me = userOperations.create("first", "first", true)
            you = userOperations.create("second", "second", true)

            myToken = credentialService.create(me).accessToken
            yourToken = credentialService.create(you).accessToken

            myClient = RestClient.builder()
                  .baseUrl("http://localhost:$port/message/text")
                  .defaultHeader(HEADER_NAME, "$BEARER$myToken")
                  .build()

            yourClient = RestClient.builder()
                  .baseUrl("http://localhost:$port/message/text")
                  .defaultHeader(HEADER_NAME, "$BEARER$yourToken")
                  .build()
      }

      @BeforeEach
      @Throws(Exception::class)
      fun setUp() {
            val stompClient = WebSocketStompClient(StandardWebSocketClient())
            stompClient.messageConverter = converter

            val myHeaders = WebSocketHttpHeaders()
            myHeaders.add(HEADER_NAME, "$BEARER$myToken")
            mySession = stompClient.connectAsync(
                  "ws://localhost:$port$HANDSHAKE_DESTINATION",
                  myHeaders,
                  object : StompSessionHandlerAdapter() {
                  }).get()

            val yourHeaders = WebSocketHttpHeaders()
            yourHeaders.add(HEADER_NAME, "$BEARER$yourToken")
            yourSession = stompClient.connectAsync(
                  "ws://localhost:$port$HANDSHAKE_DESTINATION",
                  yourHeaders,
                  object : StompSessionHandlerAdapter() {
                  }).get()
      }

      @AfterEach
      fun cleanUp() {
            mySession.disconnect()
            yourSession.disconnect()
      }

      @Test
      @Timeout(value = 3, unit = TimeUnit.SECONDS)
      @Order(1)
      fun subscribe_Expect_Echo_Welcome() {
            val myEvent = CompletableFuture<WelcomeEvent>()
            val yourEvent = CompletableFuture<WelcomeEvent>()

            mySession.subscribe(USER_QUEUE_DESTINATION, object : StompFrameHandler {

                  override fun getPayloadType(headers: StompHeaders): Type {
                        return WelcomeEvent::class.java
                  }

                  override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        myEvent.complete(payload as WelcomeEvent)
                  }
            })

            yourSession.subscribe(USER_QUEUE_DESTINATION, object : StompFrameHandler {
                  override fun getPayloadType(headers: StompHeaders): Type {
                        return WelcomeEvent::class.java
                  }

                  override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        yourEvent.complete(payload as WelcomeEvent)
                  }
            })

            assertNotNull(yourEvent.get())
            assertNotNull(myEvent.get())
//        assertNotNull(myRecord.get().localChat)
//        assertNotNull(yourRecord.get().localChat)
      }


      @Test
      @Timeout(value = 3, unit = TimeUnit.SECONDS)
      @Order(2)
      fun Post_Record_Expect_Both_Receive_Record() {
            val myEcho = CompletableFuture<WelcomeEvent>()
            val yourEcho = CompletableFuture<WelcomeEvent>()
            val myEvent = CompletableFuture<TextEvent>()
            val yourEvent = CompletableFuture<TextEvent>()

            mySession.subscribe(USER_QUEUE_DESTINATION, object : StompFrameHandler {
                  override fun getPayloadType(headers: StompHeaders): Type {
                        if (myEcho.isDone)
                              return TextEvent::class.java
                        return WelcomeEvent::class.java
                  }

                  override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        if (!myEcho.isDone) {
                              myEcho.complete(payload as WelcomeEvent)
                        } else {
                              myEvent.complete(payload as TextEvent)
                        }
                  }
            })

            yourSession.subscribe(USER_QUEUE_DESTINATION, object : StompFrameHandler {
                  override fun getPayloadType(headers: StompHeaders): Type {
                        if (yourEcho.isDone)
                              return TextEvent::class.java
                        return WelcomeEvent::class.java
                  }

                  override fun handleFrame(headers: StompHeaders, payload: Any?) {
                        if (!yourEcho.isDone) {
                              yourEcho.complete(payload as WelcomeEvent)
                        } else {
                              yourEvent.complete(payload as TextEvent)
                        }
                  }
            })

            val event = TextEvent(Chat(me, you), me, "Hello how are you")

            val received = myClient.post()
                  .contentType(MediaType.APPLICATION_JSON).body(event)
                  .retrieve().body(TextEvent::class.java)

            Assertions.assertNotNull(received)
            Assertions.assertNotNull(myEcho.get().content)
            Assertions.assertNotNull(yourEcho.get().content)

            Assertions.assertEquals(myEvent.get().chatIdentifier, yourEvent.get().chatIdentifier)
            Assertions.assertEquals(TEXT, myEvent.get().eventType)

            Assertions.assertEquals((myEvent.get() as TextEvent).content, (yourEvent.get() as TextEvent).content)
            Assertions.assertEquals("Hello how are you", (myEvent.get() as TextEvent).content)
      }
}
