package com.decade.practice.controllers.rest

import com.decade.practice.database.repository.UserRepository
import com.decade.practice.model.local.Account
import com.decade.practice.model.local.AccountEntry
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/account")
class AccountController(
      private val userRepository: UserRepository,
) {

      @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
      @GetMapping
      fun get(
            @AuthenticationPrincipal(expression = "name") username: String,
      ) = ResponseEntity.ok(
            AccountEntry(
                  Account(
                        userRepository.getByUsername(username),
                        credential = null
                  ), emptyList()
            )
      )


}
