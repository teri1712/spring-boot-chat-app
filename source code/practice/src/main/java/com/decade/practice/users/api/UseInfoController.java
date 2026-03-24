package com.decade.practice.users.api;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user-infos")
@AllArgsConstructor
public class UseInfoController {

      private final UserApi userApi;
      
      @GetMapping
      public Map<UUID, UserInfo> getUsersByIds(@Size(max = 50) @RequestParam("userId") Set<UUID> ids) {
            return userApi.getUserInfo(ids);
      }
}