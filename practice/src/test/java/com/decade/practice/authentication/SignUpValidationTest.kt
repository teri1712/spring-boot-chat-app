package com.decade.practice.authentication

import com.decade.practice.DevelopmentApplication
import com.decade.practice.controllers.advices.ExceptionControllerAdvice
import com.decade.practice.controllers.rest.AuthenticationController
import com.decade.practice.controllers.rest.MAX_USERNAME_LENGTH
import com.decade.practice.controllers.rest.MIN_USERNAME_LENGTH
import com.decade.practice.core.UserOperations
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.DefaultAvatar
import com.decade.practice.model.domain.entity.User
import com.decade.practice.model.dto.SignUpRequest
import com.decade.practice.security.jwt.JwtCredentialService
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.text.SimpleDateFormat
import java.util.*

@WebMvcTest(controllers = [AuthenticationController::class])
@ActiveProfiles("development")
@ContextConfiguration(classes = [DevelopmentApplication::class])
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(
      OutputCaptureExtension::class
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
      ExceptionControllerAdvice::class,
)
class SignUpValidationTest {

      @Autowired
      private lateinit var mockMvc: MockMvc

      @MockBean
      private lateinit var userOperations: UserOperations

      @MockBean
      private lateinit var encoder: PasswordEncoder

      @MockBean
      private lateinit var credentialService: JwtCredentialService

      @MockBean
      private lateinit var securityContextRepository: SecurityContextRepository

      @MockBean
      private lateinit var imageStore: ImageStore

      @BeforeEach
      fun setUp() {
            Mockito.`when`(encoder.encode(Mockito.anyString())).thenAnswer {
                  it.getArgument<String>(0)
            }
            Mockito.`when`(
                  userOperations.create(
                        Mockito.any<String>(),      // username
                        Mockito.any<String>(),      // password
                        Mockito.any<String>(),      // name
                        Mockito.any(),      // dob
                        Mockito.any<String>(),      // gender
                        Mockito.any(),            // avatar
                        Mockito.anyBoolean(),
                  )
            ).thenReturn(User("123", "123"))

            Mockito.`when`(imageStore.save(Mockito.any())).thenAnswer {
                  DefaultAvatar
            }
      }


      @Test
      @Throws(Exception::class)
      fun given_usernameWithSpaces_when_signUp_then_returnsValidationError() {
            val dto: SignUpRequest = SignUpRequest(
                  "user name",
                  "password",
                  "user",
                  "Male",
                  SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
            );

            val informationPart: MockMultipartFile =
                  MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        ObjectMapper().writeValueAsBytes(dto)
                  )
            mockMvc.perform(
                  MockMvcRequestBuilders.multipart("/authentication/sign-up")
                        .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                        .file(informationPart)
            )
                  .andExpect(MockMvcResultMatchers.status().isBadRequest())
                  .andExpect(
                        MockMvcResultMatchers.content()
                              .string(Matchers.containsString("Username must not contain spaces."))
                  )
      }

      @Test
      @Throws(Exception::class)
      fun given_usernameTooShort_when_signUp_then_returnsLengthValidationError() {
            val dto: SignUpRequest = SignUpRequest(
                  "user",
                  "password",
                  "user",
                  "Male",
                  SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
            );

            val informationPart: MockMultipartFile =
                  MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        ObjectMapper().writeValueAsBytes(dto)
                  )
            mockMvc.perform(
                  MockMvcRequestBuilders.multipart("/authentication/sign-up")
                        .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                        .file(informationPart)
            )
                  .andExpect(MockMvcResultMatchers.status().isBadRequest())
                  .andExpect(
                        MockMvcResultMatchers.content()
                              .string(Matchers.containsString("Username length must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"))
                  )
      }

      @Test
      @Throws(Exception::class)
      fun given_weakPassword_when_signUp_then_returnsPasswordValidationError() {
            val dto: SignUpRequest = SignUpRequest(
                  "username",
                  "pass",
                  "user",
                  "Male",
                  SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
            );

            val informationPart: MockMultipartFile =
                  MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        ObjectMapper().writeValueAsBytes(dto)
                  )
            mockMvc.perform(
                  MockMvcRequestBuilders.multipart("/authentication/sign-up")
                        .file(MockMultipartFile("file", "filename.txt", "text/plain", "file content".toByteArray()))
                        .file(informationPart)
            )
                  .andExpect(MockMvcResultMatchers.status().isBadRequest())
                  .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Password too weak")))
      }

      @Test
      @Throws(Exception::class)
      fun given_validSignupData_when_signUp_then_succeeds() {
            val dto: SignUpRequest = SignUpRequest(
                  "username",
                  "password",
                  "user",
                  "Male",
                  SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
            );

            val informationPart: MockMultipartFile =
                  MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        ObjectMapper().writeValueAsBytes(dto)
                  )
            mockMvc.perform(
                  MockMvcRequestBuilders.multipart("/authentication/sign-up")
                        .file(MockMultipartFile("file", "avatar.bmp", "image/bmp", ONE_PIXEL_BMP_BYTES))
                        .file(informationPart)
            )
                  .andExpect(MockMvcResultMatchers.status().isOk())
      }

      companion object {
            const val ONE_PIXEL_BMP_BASE64: String =
                  "Qk06AAAAAAAAADYAAAAoAAAAAQAAAAEAAAABABgAAAAAAAQAAAATCwAAEwsAAAAAAAAAAAD///8A"


            val ONE_PIXEL_BMP_BYTES: ByteArray? = Base64.getDecoder().decode(ONE_PIXEL_BMP_BASE64)

      }
}