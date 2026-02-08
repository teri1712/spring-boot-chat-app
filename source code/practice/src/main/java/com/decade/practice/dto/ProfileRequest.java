package com.decade.practice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ProfileRequest {

    @NotBlank
    @Pattern(regexp = "\\S.*")
    private String name;

    @Nullable
    private Float gender;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dob;

    @Valid
    private ImageRequest avatar;
}
