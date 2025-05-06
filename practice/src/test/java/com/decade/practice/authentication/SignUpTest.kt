package com.decade.practice.authentication

import com.decade.practice.controllers.AuthenticationController
import com.decade.practice.controllers.MAX_USERNAME_LENGTH
import com.decade.practice.controllers.MIN_USERNAME_LENGTH
import com.decade.practice.controllers.advices.ExceptionControllerAdvice
import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.UserService
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.embeddable.ImageSpec
import com.decade.practice.model.domain.entity.User
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
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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
                  ImageSpec("", "")
            }
      }


      @Test
      @Throws(Exception::class)
      fun given_usernameWithSpaces_when_signUp_then_returnsValidationError() {
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
      fun given_usernameTooShort_when_signUp_then_returnsLengthValidationError() {
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
      fun given_weakPassword_when_signUp_then_returnsPasswordValidationError() {
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
      fun given_validSignupData_when_signUp_then_succeeds() {
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