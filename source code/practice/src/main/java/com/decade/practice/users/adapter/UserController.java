package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.in.UserService;
import com.decade.practice.users.dto.SignUpRequest;
import com.decade.practice.users.dto.UserResponse;
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

    private final UserService userService;

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
    public UserResponse registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        return userService.create(signUpRequest, true);
    }
}