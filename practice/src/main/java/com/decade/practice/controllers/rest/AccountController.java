package com.decade.practice.controllers.rest;

import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.local.Account;
import com.decade.practice.model.local.AccountEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/account")
public class AccountController {

      private final UserRepository userRepository;

      public AccountController(UserRepository userRepository) {
            this.userRepository = userRepository;
      }

      @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
      @GetMapping
      public ResponseEntity<AccountEntry> get(
            @AuthenticationPrincipal(expression = "name") String username
      ) {
            return ResponseEntity.ok(
                  new AccountEntry(
                        new Account(
                              userRepository.getByUsername(username),
                              null
                        ),
                        Collections.emptyList()
                  )
            );
      }
}