package com.decade.practice.core

import com.decade.practice.database.DatabaseConfiguration
import com.decade.practice.database.transaction.ChatService
import com.decade.practice.database.transaction.UserService
import com.decade.practice.database.transaction.create
import com.decade.practice.model.domain.entity.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback

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
      fun prepareUsers() {
            Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }

            first = userOperations.create("first", "first", true)
            second = userOperations.create("second", "second", true)
      }

      @Test
      @Order(2)
      fun Get_Or_Create_Chat_Transactional_Expected_Instance_Returned() {
            val chat = chatOperations.getOrCreateChat(first.id, second.id)
            Assertions.assertNotNull(chat.interactTime)
      }
}
