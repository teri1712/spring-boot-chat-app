package com.decade.practice.api.dto;

import com.decade.practice.api.web.validation.StrongPassword;
import com.decade.practice.persistence.jpa.DefaultAvatar;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class SignUpRequest {

    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_USERNAME_LENGTH = 5;

    @Size(
            min = MIN_USERNAME_LENGTH,
            max = MAX_USERNAME_LENGTH,
            message = "Username length must be between "
                    + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH
                    + " characters"
    )
    @NotBlank(message = "Username must not be empty")
    @Pattern(regexp = "\\S+", message = "Username must not contain spaces.")
    private String username;

    @StrongPassword
    private String password;

    @NotBlank
    @NotNull
    private String name;

    @NotNull
    private Float gender;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dob;


    private ImageSpec avatar = DefaultAvatar.getInstance();

}
