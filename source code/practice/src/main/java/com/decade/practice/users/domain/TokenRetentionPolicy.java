package com.decade.practice.users.domain;

import com.decade.practice.users.application.ports.out.TokenStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@AllArgsConstructor
public class TokenRetentionPolicy {
      private final TokenStore tokenStore;

      public void apply(String username, String retained) throws AccessDeniedException {
            if (!tokenStore.has(username, retained))
                  throw new AccessDeniedException("Invalid token");
      }
}
