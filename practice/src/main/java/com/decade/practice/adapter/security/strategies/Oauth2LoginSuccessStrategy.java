package com.decade.practice.adapter.security.strategies;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Oauth2LoginSuccessStrategy implements AuthenticationSuccessHandler {

        private final String frontEndAddress;

        public Oauth2LoginSuccessStrategy(@Value("${frontend.host.address}") String frontEndAddress) {
                this.frontEndAddress = frontEndAddress;
        }

        @Override
        public void onAuthenticationSuccess(
                HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication
        ) throws IOException {
                response.sendRedirect(frontEndAddress);
        }
}