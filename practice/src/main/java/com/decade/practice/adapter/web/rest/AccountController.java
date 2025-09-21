package com.decade.practice.adapter.web.rest;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.ChatSnapshot;
import com.decade.practice.domain.entities.SyncContext;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.locals.Account;
import com.decade.practice.domain.locals.AccountEntry;
import com.decade.practice.domain.repositories.UserRepository;
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
        private final UserService userService;
        private final ChatService chatService;

        public AccountController(UserRepository userRepository, UserService userService, ChatService chatService) {
                this.userRepository = userRepository;
                this.userService = userService;
                this.chatService = chatService;
        }

        @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
        @GetMapping("/principal")
        public AccountEntry get(Principal principal) {
                return new AccountEntry(
                        new Account(
                                userRepository.findByUsername(principal.getName()),
                                null
                        ),
                        Collections.emptyList()
                );
        }

        @PostMapping("/authentication")
        public AccountEntry login(
                @AuthenticationPrincipal UserDetails userDetails
        ) {
                Account account = userService.prepareAccount(userDetails);
                User user = account.getUser();
                SyncContext syncContext = user.getSyncContext();
                List<ChatSnapshot> chatList = chatService.listChat(user)
                        .stream()
                        .map(chat -> chatService.getSnapshot(chat, user, syncContext.getEventVersion()))
                        .collect(Collectors.toList());

                return new AccountEntry(account, chatList);
        }
}