package com.decade.practice.security;

import com.decade.practice.security.jwt.JwtTokenFilter;
import com.decade.practice.security.strategy.*;
import com.decade.practice.usecases.UserOperations;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
        public static final String FILTER_CHAIN_BEAN_NAME = "DECADE_FILTER_CHAIN";

        @Value("${frontend.host.address}")
        private String frontEndAddress;

        @Configuration(proxyBeanMethods = false)
        public static class ShowUserNotFoundConfiguration implements BeanPostProcessor {
                // for debugging
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                        if (bean instanceof ProviderManager) {
                                ProviderManager providerManager = (ProviderManager) bean;
                                for (var authProvider : providerManager.getProviders()) {
                                        if (authProvider instanceof AbstractUserDetailsAuthenticationProvider) {
                                                ((AbstractUserDetailsAuthenticationProvider) authProvider).setHideUserNotFoundExceptions(false);
                                        }
                                }
                        }
                        return bean;
                }
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.eraseCredentials(false);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }

        @Bean
        public SecurityContextRepository contextRepository() {
                return new DelegatingSecurityContextRepository(
                        new HttpSessionSecurityContextRepository(),
                        new RequestAttributeSecurityContextRepository()
                );
        }

        @Bean
        public UrlBasedCorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of(frontEndAddress));
                config.setAllowedMethods(List.of("*"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean(name = FILTER_CHAIN_BEAN_NAME)
        public SecurityFilterChain filterChain(
                HttpSecurity http,
                LoginSuccessStrategy strategy,
                Oauth2LoginSuccessStrategy oauth2Strategy,
                LogoutStrategy logoutHandler,
                JwtTokenFilter jwtAuthenticationFilter,
                UserOperations userOperations
        ) throws Exception {
                http
                        .requestCache(Customizer.withDefaults())
                        .securityContext(context ->
                                context.securityContextRepository(contextRepository())
                        )
                        .cors(Customizer.withDefaults())
                        .csrf(csrf -> csrf.disable())
                        .formLogin(login ->
                                login.successHandler(strategy)
                                        .failureHandler(new LoginFailStrategy())
                                        .loginPage("/login")
                                        .permitAll()
                        )
                        .exceptionHandling(exceptionHandling ->
                                exceptionHandling.accessDeniedPage(null)
                                        .authenticationEntryPoint(new EntryPointStrategy())
                        )
                        .logout(logout ->
                                logout.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                                        .addLogoutHandler(logoutHandler)
                                        .permitAll()
                        )
                        .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .authorizeHttpRequests(authorize ->
                                authorize.requestMatchers("/image").permitAll()
                                        .requestMatchers("/authentication/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                                        .anyRequest().authenticated()
                        )
                        .oauth2Login(oauth2 -> {
                                oauth2.successHandler(oauth2Strategy);
                                oauth2.userInfoEndpoint(userInfoEndpoint -> {
                                        userInfoEndpoint.userService(
                                                new SaveOnLoadOauth2UserService(
                                                        userOperations,
                                                        new DefaultOAuth2UserService()
                                                )
                                        );
                                        userInfoEndpoint.oidcUserService(
                                                new SaveOnLoadOauth2UserService(
                                                        userOperations,
                                                        new OidcUserService()
                                                )
                                        );
                                });
                        })
                        .oauth2Client(Customizer.withDefaults())
                        .oauth2ResourceServer(oauth2 -> {
                                oauth2.bearerTokenResolver(new HeaderBearerTokenResolver("Oauth2-Token"));
                                oauth2.jwt(Customizer.withDefaults());
                        })
                        .sessionManagement(session ->
                                session
                                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                        .maximumSessions(-1)
                        );
                return http.build();
        }
}