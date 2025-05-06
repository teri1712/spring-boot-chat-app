package com.decade.practice.controllers

import com.decade.practice.core.UserOperations
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.embeddable.ImageSpec
import com.decade.practice.model.domain.entity.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

      @Autowired
      private lateinit var mockMvc: MockMvc

      @MockBean
      private lateinit var userOperations: UserOperations

      @MockBean
      private lateinit var imageStore: ImageStore

      @Test
      @DisplayName("POST /user/information - valid request should return 200 OK")
      fun given_validUserInformation_when_updateInformation_then_returnsSuccessWithUpdatedUser() {
            // Given
            val fakeUserId = UUID.randomUUID()
            val resultUser = User(
                  id = fakeUserId,
                  username = "abc",
                  password = "abc",
                  name = "Updated Name",
                  dob = Date(),
            )
            given(userOperations.update(eq(fakeUserId), any(), any(), any()))
                  .willReturn(resultUser)

            // When & Then
            mockMvc.perform(
                  post("/user/information")
                        // Simulating that the principal's "id" is the UUID we passed:
                        // In an actual scenario, you may need a custom security setup to pass the ID in.
                        .with { request ->
                              request.setAttribute("id", fakeUserId)
                              request
                        }
                        .param("name", "Updated Name")
                        .param("birthday", "2000-01-01")
                        .param("gender", "male")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
                  .andExpect(status().isOk)
                  .andExpect(jsonPath("$.id").value(fakeUserId.toString()))
                  .andExpect(jsonPath("$.name").value("Updated Name"))
      }

      @Test
      @DisplayName("POST /user/avatar - valid request should return 200 OK")
      fun given_validAvatarData_when_updateAvatar_then_returnsSuccessWithUpdatedUser() {
            // Given
            val fakeUserId = UUID.randomUUID()
            val resultUser = User(
                  id = fakeUserId,
                  name = "AvatarUser",
                  dob = Date(),
                  username = "abc",
                  password = "abc",
            )
            given(userOperations.update(eq(fakeUserId), any<ImageSpec>()))
                  .willReturn(resultUser)

            // When & Then
            mockMvc.perform(
                  post("/user/avatar")
                        // Similar approach to pass an attribute that simulates the userClaims object’s ID.
                        .with { request ->
                              request.setAttribute("id", fakeUserId)
                              request
                        }
                        // Since we're testing file upload, we pass it as a mock part:
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("file", "mockFileData")
            )
                  .andExpect(status().isOk)
                  .andExpect(jsonPath("$.id").value(fakeUserId.toString()))
                  .andExpect(jsonPath("$.name").value("AvatarUser"))
      }
}