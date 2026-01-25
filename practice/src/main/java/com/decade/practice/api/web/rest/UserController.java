package com.decade.practice.api.web.rest;

import com.decade.practice.api.dto.SignUpRequest;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.infra.security.models.DaoUser;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    // TODO: Migrate to elastic
    public List<User> findUsers(
            @RequestParam(required = true) String query
    ) {
        return userRepository.findByNameContainingAndRole(query, "ROLE_USER");
    }

    @PostMapping
    @PreAuthorize("isAnonymous()")
    // TODO: Adjust client to problem detail
    public ResponseEntity<Object> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        try {
            User user = userService.create(signUpRequest, true);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                    new DaoUser(user), signUpRequest.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            ));
            SecurityContextHolder.setContext(context);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException ex) {
            log.debug("Integrity violation", ex);

            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
            pd.setDetail("Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);

        }
    }
}