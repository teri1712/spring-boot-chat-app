package com.decade.practice.core


import com.decade.practice.database.DatabaseConfiguration
import com.decade.practice.database.repository.ChatRepository
import com.decade.practice.database.repository.EdgeRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.database.transaction.*
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.ChatEvent
import com.decade.practice.model.domain.entity.TextEvent
import com.decade.practice.model.domain.entity.User
import com.decade.practice.util.inspectPartner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.support.TransactionOperations

@DataJpaTest(
      properties = [
            "spring.datasource.url=jdbc:h2:mem:test",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database=H2"]
)
@ExtendWith(OutputCaptureExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
      ChatService::class,
      ChatEventStore::class,
      UserEventStore::class,
      UserService::class,
      DatabaseConfiguration::class
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ConversationTest {

      @Autowired
      private lateinit var eventStore: EventStore

      @Autowired
      private lateinit var edgeRepo: EdgeRepository

      @Autowired
      private lateinit var userRepo: UserRepository

      @Autowired
      private lateinit var userOperations: UserOperations

      @Autowired
      private lateinit var template: TransactionOperations

      @Autowired
      private lateinit var chatRepo: ChatRepository

      @Autowired
      private lateinit var chatOperations: ChatOperations

      @MockBean
      private lateinit var passwordEncoder: PasswordEncoder


      private fun sendEvent(from: User, to: User, message: String): ChatEvent {
            val chat = Chat(from, to)
            val event = TextEvent(chat, from, message)
            eventStore.save(event)
            return event
      }

      private lateinit var first: User
      private lateinit var second: User
      private lateinit var third: User

      @BeforeAll
      fun setUp() {

            Mockito.`when`(passwordEncoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }

            template.executeWithoutResult {
                  first = userOperations.create("first", "first")
                  second = userOperations.create("second", "second")
                  third = userOperations.create("third", "third")
            }

      }

      @BeforeEach
      fun prepare() {
            first = userRepo.getByUsername(first.username)
            second = userRepo.getByUsername(second.username)
            third = userRepo.getByUsername(third.username)
      }


      @Test
      fun given_newMessageBetweenUsers_when_eventSaved_then_createsEdgesAndUpdatesChatStats() {
            val event: ChatEvent = sendEvent(first, second, "Hello")

            Assertions.assertEquals(5, edgeRepo.count())

            val chat: Chat = chatRepo.get(event.chatIdentifier)
            Assertions.assertEquals(1, chat.firstUser.syncContext.eventVersion)
            Assertions.assertEquals(1, chat.secondUser.syncContext.eventVersion)
            Assertions.assertEquals(1, chat.messageCount)
      }

      @Test
      fun given_multipleMessages_when_queryingChats_then_ordersByLatestActivity() {
            Assertions.assertEquals(3, chatRepo.count())

            sendEvent(first, second, "Hello")
            sendEvent(third, first, "I'm fine")
            Assertions.assertEquals(3 + 2, chatRepo.count())


            Assertions.assertEquals(1 + 2, edgeRepo.findByOwner(first).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size)
            Assertions.assertEquals(2, first.syncContext.eventVersion)
            Assertions.assertEquals(1, second.syncContext.eventVersion)
            Assertions.assertEquals(1, third.syncContext.eventVersion)
            var chats: List<Chat> = chatOperations.listChat(first)

            Assertions.assertEquals(1 + 2, chats.size)
            Assertions.assertEquals(second, chats[1].inspectPartner(first))
            Assertions.assertEquals(third, chats[0].inspectPartner(first))

            Assertions.assertEquals(3 + 2 + 1 + 1, edgeRepo.count())

            sendEvent(second, first, "How are you")

            Assertions.assertEquals(3 + 2, chatRepo.count())
            Assertions.assertEquals(1 + 2 + 2, edgeRepo.findByOwner(first).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size)

            Assertions.assertEquals(3 + 2 + 1 + 1 + 2, edgeRepo.count())

            chats = chatOperations.listChat(first)

            Assertions.assertEquals(third, chats[1].inspectPartner(first))
            Assertions.assertEquals(second, chats[0].inspectPartner(first))
            Assertions.assertEquals(1 + 2, chats.size)


            Assertions.assertEquals(3 + 2, chatRepo.count())
            Assertions.assertEquals(1 + 2 + 2, edgeRepo.findByOwner(first).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size)
            Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size)
      }
}
