package com.decade.practice.infra.configs;

import com.decade.practice.adapter.session.PasswordChangeSessionInvalidator;
import com.decade.practice.application.usecases.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

@Configuration
public class SessionConfiguration {

//    @Bean
//    public GenericJackson2JsonRedisSerializer springSessionDefaultRedisSerializer() {
//        return new GenericJackson2JsonRedisSerializer();
//    }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
                return new HttpSessionEventPublisher();
        }

        @Bean
        public SessionRegistry sessionRegistry(DelegatingApplicationListener delegating) {
                SessionRegistryImpl registry = new SessionRegistryImpl();
                delegating.addListener(new GenericApplicationListenerAdapter(registry));
                return registry;
        }

        @Bean
        public PasswordChangeSessionInvalidator passwordChangeRemoveSessionsAccountListener(
                SessionRepository<? extends Session> sessionRepository,
                SessionRegistry sessionRegistry,
                TokenService credentialService
        ) {
                return new PasswordChangeSessionInvalidator(
                        sessionRegistry,
                        sessionRepository,
                        credentialService
                );
        }
}