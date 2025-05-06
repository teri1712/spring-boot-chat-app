package com.decade.practice.controllers

import com.decade.practice.database.repository.UserRepository
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.local.Conversation
import com.decade.practice.model.local.LocalChat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user")
class UserController(
      private val userRepository: UserRepository,
) {

      @GetMapping
      fun findUsersByName(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam(required = true) query: String
      ): List<Conversation> {
            val user = userRepository.getByUsername(username)
            return userRepository.findByNameContainingAndRole(query, "ROLE_USER").map { partner ->
                  val identifier = ChatIdentifier.from(user, partner)
                  Conversation(LocalChat(identifier, user.id, partner.id), partner, user)
            }
      }

}