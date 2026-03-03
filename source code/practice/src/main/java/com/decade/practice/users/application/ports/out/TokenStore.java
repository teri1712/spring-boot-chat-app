package com.decade.practice.users.application.ports.out;

public interface TokenStore {

      void evict(String username);

      void evict(String username, String refreshToken);

      boolean has(String username, String refreshToken);

      void add(String username, String... refreshTokens);


}