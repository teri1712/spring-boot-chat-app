package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.out.AuthenticationRefreshToken;
import com.decade.practice.web.security.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestRefreshToken implements AuthenticationRefreshToken {

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @Override
    public String get() {
        return TokenUtils.extractRefreshToken(getCurrentRequest());
    }
}
