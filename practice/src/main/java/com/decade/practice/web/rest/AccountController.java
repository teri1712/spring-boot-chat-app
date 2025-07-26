package com.decade.practice.web.rest;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.core.UserOperations;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.domain.ChatSnapshot;
import com.decade.practice.model.domain.SyncContext;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.local.Account;
import com.decade.practice.model.local.AccountEntry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
public class AccountController {

      private final UserRepository userRepository;
      private final UserOperations userOperations;
      private final ChatOperations chatOperations;

      public AccountController(UserRepository userRepository, UserOperations userOperations, ChatOperations chatOperations) {
            this.userRepository = userRepository;
            this.userOperations = userOperations;
            this.chatOperations = chatOperations;
      }

      @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
      @GetMapping("/principal")
      public AccountEntry get(Principal principal) {
            return new AccountEntry(
                  new Account(
                        userRepository.getByUsername(principal.getName()),
                        null
                  ),
                  Collections.emptyList()
            );
      }

      @PostMapping("/authentication")
      public AccountEntry login(
            @AuthenticationPrincipal UserDetails userDetails
      ) {
            Account account = userOperations.prepareAccount(userDetails);
            User user = account.getUser();
            SyncContext syncContext = user.getSyncContext();
            List<ChatSnapshot> chatList = chatOperations.listChat(user)
                  .stream()
                  .map(chat -> chatOperations.getSnapshot(chat, user, syncContext.getEventVersion()))
                  .collect(Collectors.toList());

            return new AccountEntry(account, chatList);
      }
}