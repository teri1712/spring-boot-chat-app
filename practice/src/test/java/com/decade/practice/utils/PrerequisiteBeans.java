package com.decade.practice.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@TestConfiguration
public class PrerequisiteBeans {

        @Bean
        @ConditionalOnMissingBean
        public PasswordEncoder encoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        @ConditionalOnMissingBean
        public ClientRegistrationRepository clientRegistrationRepository() {
                ClientRegistration registration = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                        .clientId("for-test-purpose.apps.googleusercontent.com")
                        .clientSecret("for-test-purpose")
                        .build();
                return new InMemoryClientRegistrationRepository(registration);
        }

        @Bean
        @ConditionalOnMissingBean
        public JwtDecoder jwtDecoder() {
                return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
        }

}
