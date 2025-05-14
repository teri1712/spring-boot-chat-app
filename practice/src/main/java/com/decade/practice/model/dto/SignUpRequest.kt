package com.decade.practice.model.dto

import com.decade.practice.controllers.rest.MAX_USERNAME_LENGTH
import com.decade.practice.controllers.rest.MIN_USERNAME_LENGTH
import com.decade.practice.controllers.validation.StrongPassword
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

@JsonDeserialize
data class SignUpRequest(

      @field: Size(
            min = MIN_USERNAME_LENGTH,
            max = MAX_USERNAME_LENGTH,
            message = "Username length must be between "
                    + "$MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH "
                    + "characters"
      )
      @field: NotBlank(message = "Username must not be empty")
      @field: Pattern(regexp = "\\S+", message = "Username must not contain spaces.")
      val username: String,

      @field: StrongPassword
      val password: String,

      @field: NotBlank
      val name: String,

      @field: NotBlank
      val gender: String,

      @field: Past @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      val dob: Date,
)