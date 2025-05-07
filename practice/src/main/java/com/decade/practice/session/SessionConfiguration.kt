package com.decade.practice.session

import com.decade.practice.core.TokenCredentialService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.GenericApplicationListenerAdapter
import org.springframework.security.context.DelegatingApplicationListener
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.session.Session
import org.springframework.session.SessionRepository

@Configuration
class SessionConfiguration {

//      @Bean
//      fun springSessionDefaultRedisSerializer(): GenericJackson2JsonRedisSerializer {
//            return GenericJackson2JsonRedisSerializer()
//      }

      @Bean
      fun httpSessionEventPublisher(): HttpSessionEventPublisher {
            return HttpSessionEventPublisher()
      }

      @Bean
      fun sessionRegistry(delegating: DelegatingApplicationListener): SessionRegistry {
            val registry = SessionRegistryImpl()
            delegating.addListener(GenericApplicationListenerAdapter(registry))
            return registry
      }

      /**
       * Creates a listener for password change events that invalidates sessions.
       *
       * When a user changes their password, this component ensures that:
       * - All other sessions for that user are terminated
       * - Token credentials are properly managed during the password change process
       * - Only the current session remains valid after a password change
       *
       * @param sessionRepository Repository for accessing and manipulating sessions
       * @param sessionRegistry Registry of active sessions in the application
       * @param credentialService Service for managing token-based credentials
       * @return PasswordChangeSessionInvalidator instance
       */

      @Bean
      fun passwordChangeRemoveSessionsAccountListener(
            sessionRepository: SessionRepository<out Session>,
            sessionRegistry: SessionRegistry,
            credentialService: TokenCredentialService
      ): PasswordChangeSessionInvalidator {
            return PasswordChangeSessionInvalidator(
                  sessionRegistry,
                  sessionRepository,
                  credentialService
            )
      }
}