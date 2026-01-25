package com.decade.practice.api.web.rest;

import com.decade.practice.api.dto.AccountEntryResponse;
import com.decade.practice.api.dto.AccountResponse;
import com.decade.practice.api.dto.ProfileRequest;
import com.decade.practice.api.dto.UserResponse;
import com.decade.practice.api.web.validation.StrongPassword;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

//TODO: Migrate client profile management to SPA
@AllArgsConstructor
@RestController
@Validated
// TODO: client: account -> accounts
@RequestMapping("/accounts/me")
public class AccountController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public AccountEntryResponse get(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        AccountResponse accountResponse = AccountResponse.builder().user(UserResponse.from(user)).syncContext(user.getSyncContext()).build();
        return new AccountEntryResponse(
                accountResponse,
                null
        );
    }

    @PutMapping("/profile")
    public UserResponse changeProfile(
            @RequestBody @Valid ProfileRequest profile,
            @AuthenticationPrincipal(expression = "id") UUID id
    ) throws OptimisticLockException {
        return UserResponse.from(userService.changeProfile(id, profile));
    }

    @PostMapping("/profile/password")
    public UserResponse changePassword(
            @AuthenticationPrincipal(expression = "id") UUID id,
            @RequestParam(value = "password", required = false) String password,
            @StrongPassword @RequestParam("new_password") String newPassword
    ) {
        return UserResponse.from(userService.changePassword(id, newPassword, password));
    }
}