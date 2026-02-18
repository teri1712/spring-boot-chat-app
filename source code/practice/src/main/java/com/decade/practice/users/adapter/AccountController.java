package com.decade.practice.users.adapter;

import com.decade.practice.users.adapter.validation.StrongPassword;
import com.decade.practice.users.application.ports.in.UserService;
import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.UserResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/accounts/me")
public class AccountController {

    private final UserService userService;

    @GetMapping
    public UserResponse get(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @PatchMapping("/profile")
    public UserResponse changeProfile(
            @RequestBody @Valid ProfileRequest profile,
            @AuthenticationPrincipal(expression = "id") UUID id
    ) throws OptimisticLockException {
        return userService.changeProfile(id, profile);
    }

    // TODO: Adjust client url
    @PostMapping("/password")
    public UserResponse changePassword(
            @AuthenticationPrincipal(expression = "id") UUID id,
            @RequestParam(value = "password", required = false) String password,
            @StrongPassword @RequestParam("new_password") String newPassword
    ) {
        return userService.changePassword(id, newPassword, password);
    }
}