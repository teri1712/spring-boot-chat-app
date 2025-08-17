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
                return new InMemoryClientRegistrationRepository(
                        ClientRegistration.withRegistrationId("github")
                                .clientId("dummy-client-id")
                                .clientSecret("dummy-client-secret")
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                                .scope("read:user")
                                .authorizationUri("https://github.com/login/oauth/authorize")
                                .tokenUri("https://github.com/login/oauth/access_token")
                                .userInfoUri("https://api.github.com/user")
                                .userNameAttributeName("id")
                                .clientName("GitHub")
                                .build()
                );
        }

}
