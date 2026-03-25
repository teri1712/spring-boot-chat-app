package com.decade.practice.users.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ProfileRequest {

      @Pattern(regexp = "\\S.*")
      @Nullable
      private String name;

      @Nullable
      private Float gender;

      @Past
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @Nullable
      private Date dob;

      @Nullable
      private String avatar;
}
