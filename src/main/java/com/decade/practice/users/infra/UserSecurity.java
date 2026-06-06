package com.decade.practice.users.infra;

import com.decade.practice.shared.security.jwt.JwtService;
import com.decade.practice.shared.security.jwt.JwtTokenFilter;
import com.decade.practice.users.adapter.security.strategies.EntryPointStrategy;
import com.decade.practice.users.adapter.security.strategies.LoginFailedStrategy;
import com.decade.practice.users.adapter.security.strategies.LoginSuccessStrategy;
import com.decade.practice.users.adapter.security.strategies.LogoutStrategy;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class UserSecurity extends GlobalAuthenticationConfigurerAdapter {

    @Value("${frontend.host.address}")
    private String frontEndAddress;

    @Configuration(proxyBeanMethods = false)
    public static class ShowUserNotFoundConfiguration implements BeanPostProcessor {
        // TODO: Will be removed latter
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

    @Bean
    public SecurityFilterChain userChain(
        HttpSecurity http,
        LoginSuccessStrategy successStrategy,
        LoginFailedStrategy failedStrategy,
        LogoutStrategy logoutHandler,
        JwtService jwtService
    ) throws Exception {
        http
            .securityMatcher("/tokens/**", "/users/**", "/login", "/logout", "/profiles/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .requestCache(Customizer.withDefaults())
            .securityContext(context ->
                context.securityContextRepository(new RequestAttributeSecurityContextRepository())
            )
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(login ->
                login.successHandler(successStrategy)
                    .failureHandler(failedStrategy)
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
            .addFilterAfter(new JwtTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers(HttpMethod.POST, "/users").permitAll()
                    .requestMatchers("/tokens/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> {
                oauth2.bearerTokenResolver(new HeaderBearerTokenResolver("Oauth2-Token"));
                oauth2.jwt(Customizer.withDefaults());
            })
            .sessionManagement(session ->
                session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}