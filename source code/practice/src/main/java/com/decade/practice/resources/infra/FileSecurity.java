package com.decade.practice.resources.infra;

import com.decade.practice.shared.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@EnableMethodSecurity
@Configuration
public class FileSecurity {

    @Bean
    public SecurityFilterChain fileChain(
        HttpSecurity http,
        JwtTokenFilter jwtAuthenticationFilter
    ) throws Exception {
        http
            .securityMatcher("/files/**")
            .requestCache(Customizer.withDefaults())
            .securityContext(context ->
                context.securityContextRepository(new RequestAttributeSecurityContextRepository())
            )
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedPage(null)
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
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