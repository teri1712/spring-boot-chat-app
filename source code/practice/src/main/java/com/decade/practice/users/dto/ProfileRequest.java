package com.decade.practice.users.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProfileRequest {

      @Pattern(regexp = "\\S.*")
      @Nullable
      private String name;

      @Nullable
      private Float gender;

      @Past
      @Nullable
      private Instant dob;

      @Nullable
      private String avatar;
}
