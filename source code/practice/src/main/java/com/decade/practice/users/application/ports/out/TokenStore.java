package com.decade.practice.users.application.ports.out;

import java.util.List;

public interface TokenStore {

      List<String> evict(String username);

      void evict(String username, String refreshToken);

      boolean has(String username, String refreshToken);

      void add(String username, String... refreshTokens);


}