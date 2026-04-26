package com.decade.practice.shared.security;

import java.time.Duration;

public interface TokenService {

    UserClaims decodeToken(String token);

    String encodeToken(UserClaims userClaims, Duration duration);

}
