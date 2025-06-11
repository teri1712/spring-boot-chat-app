package com.decade.practice.jpa

import com.decade.practice.DevelopmentApplication
import com.decade.practice.core.TokenCredentialService
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = [DevelopmentApplication::class])
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

      @MockBean
      lateinit var credentialService: TokenCredentialService

      @BeforeEach
      fun setUp() {
            Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }
//            userRepo.saveAndFlush(mockUser())

      }

      @Test
      fun testInsert() {
            userOperations.create("first", "first", true)
      }
}
