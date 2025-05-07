package com.decade.practice.authentication

import com.decade.practice.MockEndpoints
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.ChatService
import com.decade.practice.database.transaction.UserService
import com.decade.practice.model.domain.entity.User
import com.decade.practice.security.DaoUserDetailsService
import com.decade.practice.security.SaveOnLoadOauth2UserService
import com.decade.practice.security.SecurityConfiguration
import com.decade.practice.security.jwt.JwtCredentialService
import com.decade.practice.security.jwt.JwtTokenFilter
import com.decade.practice.security.strategy.LoginSuccessStrategy
import com.decade.practice.security.strategy.LogoutStrategy
import com.decade.practice.session.SessionConfiguration
import com.decade.practice.utils.ErrorMessageMatcher
import com.decade.practice.utils.TokenUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [MockEndpoints::class])
@ExtendWith(OutputCaptureExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
      SecurityConfiguration::class,
      SessionConfiguration::class,
      LoginSuccessStrategy::class,
      JwtTokenFilter::class,
      DaoUserDetailsService::class,
      JwtCredentialService::class,
      MockEndpoints::class,
      UserService::class,
      RedisAutoConfiguration::class,
      SessionAutoConfiguration::class,
      SaveOnLoadOauth2UserService::class
)
class SecurityFilterTest {
      @MockBean
      lateinit var userRepo: UserRepository

      @MockBean
      lateinit var userService: UserService

      @MockBean
      lateinit var chatService: ChatService

      @MockBean
      lateinit var logoutHandler: LogoutStrategy

      @Autowired
      lateinit var mockMvc: MockMvc

      @Autowired
      lateinit var encoder: PasswordEncoder

      @Autowired
      lateinit var credentialService: JwtCredentialService

      @BeforeEach
      fun setUp() {
            val user = User(USERNAME, encoder.encode(PASSWORD))
            Mockito.`when`(userRepo.getByUsername(USERNAME)).thenReturn(user)
      }

      @Test
      @Throws(Exception::class)
      fun given_nonExistentUsername_when_login_then_failsWithUnauthorized() {
            mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user("vcl").password(PASSWORD))
                  .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                  .andExpect(ErrorMessageMatcher.errorMessage("Username not found"))
      }

      @Test
      @Throws(Exception::class)
      fun given_validCredentials_when_login_then_succeeds() {
            mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user(USERNAME).password(PASSWORD))
                  .andExpect(MockMvcResultMatchers.status().isOk())
      }

      @Test
      @Throws(Exception::class)
      fun given_validUsernameWithInvalidPassword_when_login_then_failsWithUnauthorized() {
            mockMvc.perform(
                  SecurityMockMvcRequestBuilders.formLogin()
                        .user(USERNAME)
                        .password("vcl")
            )
                  .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                  .andExpect(ErrorMessageMatcher.errorMessage("Wrong password"))
      }

      @Test
      @Throws(Exception::class)
      fun given_validJwtToken_when_requestProtectedResource_then_succeeds() {
            val user = userRepo.getByUsername(USERNAME)
            val accessToken = credentialService.create(user).accessToken

            mockMvc.perform(
                  MockMvcRequestBuilders.get("/mock")
                        .header(TokenUtils.HEADER_NAME, "${TokenUtils.BEARER} $accessToken")
            )
                  .andExpect(MockMvcResultMatchers.status().isOk())
      }

      companion object {
            private const val USERNAME = "mock_username"
            private const val PASSWORD = "mock_password"
      }
}