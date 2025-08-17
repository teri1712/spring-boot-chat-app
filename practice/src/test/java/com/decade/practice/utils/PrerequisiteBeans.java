package com.decade.practice.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@TestConfiguration
public class PrerequisiteBeans {

        @Bean
        @ConditionalOnMissingBean
        public PasswordEncoder encoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
                return new InMemoryClientRegistrationRepository(clientRegistration());
        }

        @Bean
        @ConditionalOnMissingBean
        public ClientRegistration clientRegistration() {
                return ClientRegistration.withRegistrationId("google")
                        .clientId("vcl.apps.googleusercontent.com")
                        .clientSecret("vcl")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .scope("openid", "profile", "email")
                        .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                        .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                        .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                        .userNameAttributeName("sub")
                        .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                        .clientName("Google")
                        .build();
        }
}
