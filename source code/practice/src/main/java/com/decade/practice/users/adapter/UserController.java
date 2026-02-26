package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.in.ProfileService;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final ProfileService profileService;

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleException(DataIntegrityViolationException ex) {
        log.debug("Integrity violation", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setDetail("Username already exists");
        return pd;
    }

    @PostMapping
    // TODO: Adjust client to problem detail
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        return profileService.create(signUpRequest, true);
    }
}