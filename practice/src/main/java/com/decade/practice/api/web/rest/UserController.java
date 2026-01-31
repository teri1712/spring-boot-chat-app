package com.decade.practice.api.web.rest;

import com.decade.practice.api.dto.SignUpRequest;
import com.decade.practice.api.dto.UserResponse;
import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.application.usecases.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final SearchService searchService;
    private final UserService userService;

    @GetMapping
    // TODO: Migrate to elastic
    public List<UserResponse> findUsers(
            @RequestParam() String query
    ) {
        return searchService.searchUsers(query);
    }

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