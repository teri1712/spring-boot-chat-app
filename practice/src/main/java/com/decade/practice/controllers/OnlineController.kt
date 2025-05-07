package com.decade.practice.controllers

import com.decade.practice.core.OnlineStatistic
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/online")
class OnlineController(private val stat: OnlineStatistic) {

      @GetMapping
      fun listOnline(principal: Principal) = stat.getOnlineList(principal.name).also {
            println(ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(it))
      }

      @GetMapping("/{username}")
      fun get(@PathVariable username: String) = stat.get(username)


}