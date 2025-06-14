package com.decade.practice.core

import com.decade.practice.DevelopmentApplication
import com.decade.practice.database.DatabaseConfiguration
import com.decade.practice.database.transaction.ChatService
import com.decade.practice.database.transaction.UserService
import com.decade.practice.database.transaction.create
import com.decade.practice.model.domain.entity.User
import com.decade.practice.security.jwt.JwtCredentialService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = [DevelopmentApplication::class])
@ExtendWith(OutputCaptureExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(

      RedisAutoConfiguration::class,
      JwtCredentialService::class,
      ChatService::class,
      UserService::class,
      DatabaseConfiguration::class
)
class ChatOperationsTest {

      @Autowired
      lateinit var chatOperations: ChatOperations

      @Autowired
      lateinit var userOperations: UserOperations

      @MockBean
      lateinit var encoder: PasswordEncoder

      lateinit var first: User
      lateinit var second: User

      @Test
      @Rollback(false)
      @Order(1)
      fun prepare() {
            Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }

            first = userOperations.create("first", "first", true)
            second = userOperations.create("second", "second", true)
      }

      @Test
      @Order(2)
      fun given_twoUsers_when_getOrCreateChat_then_returnsChatInstance() {
            val chat = chatOperations.getOrCreateChat(first.id, second.id)
            Assertions.assertNotNull(chat.interactTime)
      }


      @Autowired
      lateinit var redisTemplate: RedisTemplate<Any, Any>

      @AfterAll
      fun tearDown() {

            redisTemplate.execute({ conn: RedisConnection? ->
                  conn!!.flushDb()
                  null
            })

      }
}
