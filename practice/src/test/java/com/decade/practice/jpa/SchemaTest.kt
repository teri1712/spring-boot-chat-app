package com.decade.practice.jpa

import com.decade.practice.core.UserOperations
import com.decade.practice.database.DatabaseConfiguration
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.UserService
import com.decade.practice.database.transaction.create
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder

@DataJpaTest(
      properties = [
            "spring.datasource.url=jdbc:h2:mem:test",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database=H2"]
)
@ExtendWith(OutputCaptureExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(UserService::class, DatabaseConfiguration::class)
class SchemaTest {

      @Autowired
      lateinit var entityManager: TestEntityManager

      @Autowired
      lateinit var userRepo: UserRepository

      @Autowired
      lateinit var userOperations: UserOperations

      @MockBean
      lateinit var encoder: PasswordEncoder

      @BeforeEach
      fun setUp() {
            Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }
//            userRepo.saveAndFlush(mockUser())

            userOperations.create("zzz", "zzz", true)

      }

      @Test
      fun given_duplicateUsername_when_insertingUser_then_throwsUniqueConstraintException() {
//            Assertions.assertThrows(
//                  ConstraintViolationException::class.java
//            ) { entityManager.persistAndFlush(mockUser()) }
      }
//
//      @Test
//      fun Insert_Chat_By_Setting_MapsId_Fields_Expected_Id_Derived() {
//            var first = create("first", "first")
//            var second = create("second", "second")
//
//            first = userRepo.save(first)
//            second = userRepo.save(second)
//
//            Assertions.assertNotNull(first.id)
//            Assertions.assertNotNull(second.id)
//            val chat = Chat(first, second)
//            // test purpose, no cascade on persist
//            chat.firstUser = first
//            chat.secondUser = second
//
//            entityManager.persistAndFlush(chat)
//
//            Assertions.assertNotNull(chat.identifier.firstUser)
//            Assertions.assertNotNull(chat.identifier.secondUser)
//      }
}
