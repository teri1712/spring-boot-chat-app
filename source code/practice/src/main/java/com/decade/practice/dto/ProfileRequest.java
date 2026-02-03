package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ProfileRequest {

    @NotBlank
    @NotNull
    private String name;

    @NotNull
    private Float gender;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dob;

    private ImageSpec avatar;
}
