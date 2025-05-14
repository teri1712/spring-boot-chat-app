package com.decade.practice.security.jwt;

import com.decade.practice.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JwtRequestMatcher implements RequestMatcher {
      @Override
      public boolean matches(HttpServletRequest request) {
            return TokenUtils.INSTANCE.hasToken(request);
      }
}
