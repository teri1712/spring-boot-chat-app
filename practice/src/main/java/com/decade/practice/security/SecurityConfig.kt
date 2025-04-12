package com.decade.practice.security

import com.decade.practice.security.jwt.JwtAuthenticationFilter
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.context.DelegatingSecurityContextRepository
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository

const val FILTER_CHAIN_BEAN_NAME: String = "DECADE_FILTER_CHAIN"


@EnableMethodSecurity
@Configuration
class SecurityConfig : GlobalAuthenticationConfigurerAdapter() {
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

    @Bean(name = [FILTER_CHAIN_BEAN_NAME])
    @Throws(Exception::class)
    fun filterChain(
        http: HttpSecurity,
        strategy: LoginSuccessStrategy,
        logoutHandler: LogoutStrategy,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
        oauth2UserService: SaveOnLoadOauth2UserService
    ): SecurityFilterChain {
        http
            .requestCache(Customizer.withDefaults())
            .securityContext { context ->
                context.securityContextRepository(contextRepository())
            }
            .csrf { csrf -> csrf.disable() }
            .formLogin { login ->
                login.successHandler(strategy)
                    .failureHandler(FormLoginFailHandler())
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
                oauth2.successHandler(strategy)
                oauth2.userInfoEndpoint { userInfoEndpoint ->
                    userInfoEndpoint.userService(oauth2UserService)
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