package com.decade.practice.ws

import com.decade.practice.database.ChatOperations
import com.decade.practice.database.UserOperations
import com.decade.practice.database.transaction.create
import com.decade.practice.model.TypeEvent
import com.decade.practice.model.entity.*
import com.decade.practice.security.jwt.BEARER
import com.decade.practice.security.jwt.HEADER_NAME
import com.decade.practice.util.JwtCredentialService
import com.decade.practice.util.toIdentifier
import com.decade.practice.websocket.CHAT_HEADER
import com.decade.practice.websocket.HANDSHAKE_DESTINATION
import com.decade.practice.websocket.TYPING_DESTINATION
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.jpa.database=H2"]
)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TypeEventTest {

    @LocalServerPort
    val port = 0

    @Autowired
    lateinit var converter: MessageConverter

    @Autowired
    lateinit var userOperations: UserOperations

    @Autowired
    lateinit var chatOperations: ChatOperations

    @Autowired
    lateinit var credentialService: JwtCredentialService

    lateinit var me: User
    lateinit var you: User
    lateinit var chat: Chat
    lateinit var myToken: String
    lateinit var yourToken: String
    lateinit var mySession: StompSession
    lateinit var yourSession: StompSession

    @BeforeAll
    fun beforeAll() {
        me = userOperations.create("first", "first", true)
        you = userOperations.create("second", "second", true)
        chat = chatOperations.getOrCreateChat(toIdentifier(me.id, you.id))

        myToken = credentialService.create(me).accessToken
        yourToken = credentialService.create(you).accessToken

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
    @Order(2)
    @Throws(
        Exception::class
    )
    fun Type_Expect_Both_Receive_Event() {
        val myEvent = CompletableFuture<TypeEvent>()
        val yourEvent = CompletableFuture<TypeEvent>()

        val myHeaders = StompHeaders()
        myHeaders.destination = TYPING_DESTINATION
        myHeaders.set(CHAT_HEADER, chat.identifier.toString())

        val yourHeaders = StompHeaders()
        yourHeaders.destination = TYPING_DESTINATION
        yourHeaders.set(CHAT_HEADER, chat.identifier.toString())

        mySession.subscribe(myHeaders, object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type {
                return TypeEvent::class.java
            }

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                if (payload == null)
                    return
                myEvent.complete(payload as TypeEvent)
            }
        })

        yourSession.subscribe(yourHeaders, object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type {
                return TypeEvent::class.java
            }

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                if (payload == null)
                    return
                yourEvent.complete(payload as TypeEvent)
            }
        })
        Thread.sleep(200)

        val h = StompHeaders()
        h.set(CHAT_HEADER, chat.identifier.toString())
        h.destination = TYPING_DESTINATION
        yourSession.send(h, "Hello")

        assertNotNull(myEvent.get())
        assertNotNull(yourEvent.get())
        assertEquals(yourEvent.get(), myEvent.get())
    }
}
