package com.decade.practice.security

import com.decade.practice.core.UserOperations
import com.decade.practice.security.jwt.JwtTokenFilter
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.context.DelegatingSecurityContextRepository
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

const val FILTER_CHAIN_BEAN_NAME: String = "DECADE_FILTER_CHAIN"


@EnableMethodSecurity
@Configuration
class SecurityConfiguration : GlobalAuthenticationConfigurerAdapter() {
      @Configuration(proxyBeanMethods = false)
      class ShowUserNotFoundConfiguration : BeanPostProcessor {

            // for debugging
            @Throws(BeansException::class)
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
                  if (bean is ProviderManager)
                        for (authProvider in bean.providers)
                              if (authProvider is AbstractUserDetailsAuthenticationProvider)
                                    authProvider.isHideUserNotFoundExceptions = false


                  return bean
            }
      }

      @Throws(Exception::class)
      override fun configure(auth: AuthenticationManagerBuilder) {
            auth.eraseCredentials(false)
      }

      @Bean
      fun passwordEncoder(): PasswordEncoder {
            return BCryptPasswordEncoder(10)
      }

      @Bean
      fun contextRepository(): SecurityContextRepository {
            return DelegatingSecurityContextRepository(
                  HttpSessionSecurityContextRepository(),
                  RequestAttributeSecurityContextRepository()
            )
      }

      @Bean
      fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
            val config = CorsConfiguration().apply {
                  allowedOrigins = listOf("http://localhost:4200")
                  allowedMethods = listOf("*")
                  allowedHeaders = listOf("*")
                  allowCredentials = true
                  maxAge = 3600L
            }

            return UrlBasedCorsConfigurationSource().apply {
                  registerCorsConfiguration("/**", config)
            }
      }

      @Bean(name = [FILTER_CHAIN_BEAN_NAME])
      @Throws(Exception::class)
      fun filterChain(
            http: HttpSecurity,
            strategy: LoginSuccessStrategy,
            oauth2Strategy: Oauth2LoginSuccessStrategy,
            logoutHandler: LogoutStrategy,
            jwtAuthenticationFilter: JwtTokenFilter,
            userOperations: UserOperations
      ): SecurityFilterChain {
            http
                  .requestCache(Customizer.withDefaults())
                  .securityContext { context ->
                        context.securityContextRepository(contextRepository())
                  }
                  .cors(withDefaults())
                  .csrf { csrf -> csrf.disable() }
                  .formLogin { login ->
                        login.successHandler(strategy)
                              .failureHandler(LoginFailStrategy())
                              .permitAll()
                  }
                  .exceptionHandling { exceptionHandling ->
                        exceptionHandling.accessDeniedPage(null)
                              .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                  }
                  .logout { logout: LogoutConfigurer<HttpSecurity?> ->
                        logout.logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                              .addLogoutHandler(logoutHandler)
                              .permitAll()
                  }
                  .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                  .authorizeHttpRequests { authorize ->
                        authorize.requestMatchers("/image").permitAll()
                              .requestMatchers("/authentication/**").permitAll()
                              .anyRequest().authenticated()
                  }
                  .oauth2Login { oauth2 ->
                        oauth2.successHandler(oauth2Strategy)
                        oauth2.userInfoEndpoint { userInfoEndpoint ->
                              userInfoEndpoint.userService(
                                    SaveOnLoadOauth2UserService(
                                          userOperations,
                                          DefaultOAuth2UserService()
                                    )
                              )
                              userInfoEndpoint.oidcUserService(
                                    SaveOnLoadOauth2UserService(
                                          userOperations,
                                          OidcUserService()
                                    )
                              )
                        }
                  }
                  .oauth2Client { }
                  .oauth2ResourceServer { oauth2 ->
                        oauth2.bearerTokenResolver(HeaderBearerTokenResolver("Oauth2-AccessToken"))
                        oauth2.jwt {
                        }
                  }
                  .sessionManagement { session ->
                        session
                              .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                              .maximumSessions(-1)
                  }
            return http.build()
      }
}