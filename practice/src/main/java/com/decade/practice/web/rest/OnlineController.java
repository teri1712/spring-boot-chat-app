package com.decade.practice.web.rest;

import com.decade.practice.core.OnlineStatistic;
import com.decade.practice.model.OnlineStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/online")
public class OnlineController {

      private final OnlineStatistic stat;

      public OnlineController(OnlineStatistic stat) {
            this.stat = stat;
      }

      @GetMapping
      public List<OnlineStatus> listOnline(Principal principal) {
            List<OnlineStatus> result = stat.getOnlineList(principal.getName());
            try {
                  System.out.println(new ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(result));
            } catch (Exception e) {
                  // Ignore any serialization errors
            }
            return result;
      }

      @GetMapping("/{username}")
      public OnlineStatus get(@PathVariable String username) {
            return stat.get(username);
      }
}