package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.UserService;
import com.decade.practice.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    // TODO: Adjust client to problem detail
    public ResponseEntity<Object> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        try {
            userService.create(signUpRequest, true);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException ex) {
            log.debug("Integrity violation", ex);

            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setDetail("Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);

        }
    }
}