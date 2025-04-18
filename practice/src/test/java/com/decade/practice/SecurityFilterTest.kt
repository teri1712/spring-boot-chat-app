package com.decade.practice

import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.ChatService
import com.decade.practice.database.transaction.UserService
import com.decade.practice.model.entity.User
import com.decade.practice.security.*
import com.decade.practice.security.jwt.BEARER
import com.decade.practice.security.jwt.HEADER_NAME
import com.decade.practice.security.jwt.JwtAuthenticationFilter
import com.decade.practice.security.model.JwtUser
import com.decade.practice.session.SessionConfig
import com.decade.practice.util.JwtCredentialService
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
import java.util.*

@WebMvcTest(controllers = [MockEndpoints::class])
@ExtendWith(OutputCaptureExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
    SecurityConfig::class,
    SessionConfig::class,
    LoginSuccessStrategy::class,
    JwtAuthenticationFilter::class,
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
    fun Login_Will_Fail() {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user("vcl").password(PASSWORD))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(ErrorMessageMatcher.errorMessage("Username not found"))
    }

    @Test
    @Throws(Exception::class)
    fun Login_Success() {
        mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user(USERNAME).password(PASSWORD))
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @Throws(Exception::class)
    fun Login_With_Wrong_Password() {
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
    fun Authenticated_With_JWT_Token() {
        val user = userRepo.getByUsername(USERNAME)
        val claims = JwtUser(user)
        val accessToken = credentialService.create(claims).accessToken

        mockMvc.perform(
            MockMvcRequestBuilders.get("/mock")
                .header(HEADER_NAME, "$BEARER $accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    companion object {
        private const val USERNAME = "mock_username"
        private const val PASSWORD = "mock_password"
    }
}