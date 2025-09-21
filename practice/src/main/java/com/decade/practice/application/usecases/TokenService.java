package com.decade.practice.application.usecases;

import com.decade.practice.domain.TokenCredential;
import com.decade.practice.domain.entities.User;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface TokenService {

        void validate(String refreshToken) throws AccessDeniedException;

        List<String> evict(String username);

        void evict(String username, String refreshToken);

        void add(String username, String refreshToken);

        TokenCredential create(User user, String refreshToken);

}