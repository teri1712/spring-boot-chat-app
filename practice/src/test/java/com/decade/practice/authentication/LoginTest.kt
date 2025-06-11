package com.decade.practice.authentication

import com.decade.practice.DevelopmentApplication
import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.create
import com.decade.practice.model.domain.entity.User
import com.decade.practice.model.domain.entity.WELCOME
import com.decade.practice.model.local.AccountEntry
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import kotlin.test.assertNotNull


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("development")
@ContextConfiguration(classes = [DevelopmentApplication::class])
@ExtendWith(
      OutputCaptureExtension::class
)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginTest {

      @LocalServerPort
      val port = 0

      @Autowired
      lateinit var userOperations: UserOperations

      @Autowired
      lateinit var passwordEncoder: PasswordEncoder

      @Autowired
      lateinit var userRepository: UserRepository

      lateinit var user: User

      lateinit var client: RestClient

      @BeforeAll
      fun setUp() {
            user = userOperations.create("abc", "abc")
      }


      @Test
      @Throws(Exception::class)
      fun given_validCredentials_when_login_then_returnsAccountWithChatData() {
            client = RestClient.builder()
                  .baseUrl("http://localhost:$port/login")
                  .build()


            val form: MultiValueMap<String, String> = LinkedMultiValueMap()
            form.add("username", "abc")
            form.add("password", "abc")

            val received = client.post()
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form)
                  .retrieve().body(AccountEntry::class.java)

            assertNotNull(received)
            assertNotNull(received.account)
            assertNotNull(received.chatSnapshots)
            assertNotNull(received.chatSnapshots.size == 1)
            assertNotNull(received.chatSnapshots[0].eventList.size == 1)
            assertNotNull(received.chatSnapshots[0].eventList[0].edges.size == 1)
            assertNotNull(received.chatSnapshots[0].eventList[0].eventType == WELCOME)

      }

      @Test
      @Throws(Exception::class)
      fun given_invalidCredentials_when_login_then_throwsUnauthorized() {

            client = RestClient.builder()
                  .baseUrl("http://localhost:$port/login")
                  .build()


            val form: MultiValueMap<String, String> = LinkedMultiValueMap()
            form.add("username", "abc")
            form.add("password", "zzz")

            assertThrows<HttpClientErrorException.Unauthorized> {
                  client.post()
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form)
                        .retrieve().body(AccountEntry::class.java)
            }
      }

}
