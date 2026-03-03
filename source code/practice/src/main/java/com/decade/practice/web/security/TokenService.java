package com.decade.practice.web.security;

public interface TokenService {

      UserClaims decodeToken(String token);

      String encodeToken(UserClaims userClaims, Long duration);

}
