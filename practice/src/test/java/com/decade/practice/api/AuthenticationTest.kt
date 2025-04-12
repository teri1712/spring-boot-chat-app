package com.decade.practice.api

import com.decade.practice.database.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.UserService
import com.decade.practice.database.transaction.create
import com.decade.practice.endpoints.ExceptionControllerAdvice
import com.decade.practice.endpoints.auth.AuthenticationController
import com.decade.practice.endpoints.auth.MAX_USERNAME_LENGTH
import com.decade.practice.endpoints.auth.MIN_USERNAME_LENGTH
import com.decade.practice.image.ImageStore
import com.decade.practice.model.entity.User
import com.decade.practice.model.entity.WELCOME
import com.decade.practice.model.local.AccountEntry
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.net.URL
import java.util.*
import kotlin.test.assertNotNull

@WebMvcTest(controllers = [AuthenticationController::class])
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(
    OutputCaptureExtension::class
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
    UserService::class,
    ExceptionControllerAdvice::class,
)
class SignUpTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userOperations: UserOperations

    @MockBean
    private lateinit var encoder: PasswordEncoder

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var contextRepo: SecurityContextRepository

    @MockBean
    private lateinit var imageStore: ImageStore

    @BeforeEach
    fun mockBeans() {
        Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
            it.getArgument<String>(0)
        }
        Mockito.`when`(
            userOperations.create(
                Mockito.anyString(),      // username
                Mockito.anyString(),      // password
                Mockito.anyString(),      // name
                Mockito.any(),      // dob
                Mockito.anyString(),      // gender
                Mockito.any(),            // avatar (or use a more specific matcher if needed)
                Mockito.any(),            // avatar (or use a more specific matcher if needed)
            )
        ).thenReturn(User("123", "123"))

        Mockito.`when`(imageStore.save(Mockito.any())).thenAnswer {
            URL("http://localhost:8080")
        }
    }


    @Test
    @Throws(Exception::class)
    fun SignUp_Expect_Username_Validation_Non_Space_Error() {
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/authentication/sign-up")
                .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                .param("username", "user name")
                .param("password", "password")
                .param("name", "user")
                .param("gender", "Male")
                .param("dob", "2021-07-20")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString("Username must not contain spaces."))
            )
    }

    @Test
    @Throws(Exception::class)
    fun SignUp_Expect_Username_Validation_Length_Error() {
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/authentication/sign-up")
                .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                .param("username", "user")
                .param("password", "password")
                .param("name", "user")
                .param("gender", "Male")
                .param("dob", "2021-07-20")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(
                MockMvcResultMatchers.content()
                    .string(Matchers.containsString("Username length must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"))
            )
    }

    @Test
    @Throws(Exception::class)
    fun SignUp_Expect_Password_Validation_Error() {
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/authentication/sign-up")
                .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))

                .param("username", "username")
                .param("name", "user")
                .param("password", "pass")
                .param("gender", "Male")
                .param("dob", "2021-07-20")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Password too weak")))
    }

    @Test
    @Throws(Exception::class)
    fun SignUp_Expect_Success() {
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/authentication/sign-up")
                .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                .param("username", "username")
                .param("password", "password")
                .param("name", "user")
                .param("gender", "Male")
                .param("dob", "2021-07-20")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }
}


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.jpa.database=H2"]
)
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
    fun Login_With_Valid_Credential() {
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
    fun Login_With_InValid_Credential() {

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
