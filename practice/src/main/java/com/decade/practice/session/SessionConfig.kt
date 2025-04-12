package com.decade.practice.session

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
class SessionConfig {

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
            delegating.addListener(GenericApplicationListenerAdapter(registry)) // catch session events only
            return registry
      }

      @Bean
      fun passwordChangeRemoveSessionsAccountListener(
            sessionRepository: SessionRepository<out Session>,
            sessionRegistry: SessionRegistry
      ): PasswordChangeRemoveSessionsAccountListener {
            return PasswordChangeRemoveSessionsAccountListener(sessionRegistry, sessionRepository)
      }
}