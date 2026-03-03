package com.decade.practice.users.application.ports.in;

import com.decade.practice.users.dto.AccountResponse;
import org.springframework.security.access.AccessDeniedException;

public interface TokenSessionService {

      AccountResponse login(String username);

      String refresh(String refreshToken) throws AccessDeniedException;

      void logout(String username, String refreshToken);

}
