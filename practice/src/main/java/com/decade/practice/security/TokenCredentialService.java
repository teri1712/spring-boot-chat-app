package com.decade.practice.security;

import com.decade.practice.model.TokenCredential;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.security.model.UserClaims;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface TokenCredentialService {

        void validate(String refreshToken) throws AccessDeniedException;

        List<String> evict(String username);

        void evict(String username, String refreshToken);

        void add(String username, String refreshToken);

        UserClaims decodeToken(String token);

        TokenCredential create(UserClaims claims, String refreshToken);

        TokenCredential create(User user, String refreshToken);

        List<String> get(String username);
}