package com.decade.practice.web.rest;

import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.local.Conversation;
import com.decade.practice.model.local.LocalChat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

      private final UserRepository userRepository;

      public UserController(UserRepository userRepository) {
            this.userRepository = userRepository;
      }

      @GetMapping
      public List<Conversation> findUsersByName(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam(required = true) String query
      ) {
            User user = userRepository.getByUsername(username);
            List<User> partners = userRepository.findByNameContainingAndRole(query, "ROLE_USER");
            List<Conversation> conversations = new ArrayList<>();

            for (User partner : partners) {
                  ChatIdentifier identifier = ChatIdentifier.from(user, partner);
                  conversations.add(new Conversation(new LocalChat(identifier, user.getId()), partner, user));
            }

            return conversations;
      }
}