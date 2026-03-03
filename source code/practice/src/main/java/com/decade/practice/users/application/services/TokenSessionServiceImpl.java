package com.decade.practice.users.application.services;

import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenGenerator;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.User;
import com.decade.practice.users.dto.AccessToken;
import com.decade.practice.users.dto.AccountResponse;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.dto.mapper.UserMapper;
import com.decade.practice.web.security.UserClaims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenSessionServiceImpl implements TokenSessionService {


      private final TokenStore tokenStore;
      private final UserRepository users;
      private final TokenGenerator tokenGenerator;
      private final UserMapper userMapper;


      private UserClaims validate(String refreshToken) throws AccessDeniedException {
            UserClaims claims;
            try {
                  claims = tokenGenerator.decode(refreshToken);
            } catch (JwtException e) {
                  throw new AccessDeniedException("Token expired", e);
            }
            if (!tokenStore.has(claims.username(), refreshToken)) {
                  throw new AccessDeniedException("Token expired");
            }
            return claims;
      }

      @Override
      public String refresh(String refreshToken) throws AccessDeniedException {
            UserClaims claims = validate(refreshToken);
            return tokenGenerator.generateRefreshToken(claims);
      }

      @Override
      public void logout(String username, String refreshToken) {
            tokenStore.evict(username, refreshToken);
      }

      @Override
      public AccountResponse login(String username) {
            User user = users.findByUsername(username).orElseThrow();
            ProfileResponse profileResponse = userMapper.map(user);
            UserClaims claims = new UserClaims(
                      user.getId(),
                      user.getUsername(),
                      user.getName(),
                      user.getAvatar());
            AccessToken credential = tokenGenerator.generate(claims);
            return new AccountResponse(profileResponse, credential);
      }
}
