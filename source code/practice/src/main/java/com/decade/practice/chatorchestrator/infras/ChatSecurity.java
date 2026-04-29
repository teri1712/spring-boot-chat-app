package com.decade.practice.chatorchestrator.infras;

import com.decade.practice.shared.security.jwt.JwtService;
import com.decade.practice.shared.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@EnableMethodSecurity
@Configuration
public class ChatSecurity {

    @Bean
    public SecurityFilterChain chatChain(
        HttpSecurity http,
        JwtService jwtService
    ) throws Exception {
        http
            .securityMatcher("/groups/**", "/direct-chats/**", "/chats/**")
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
            .addFilterAfter(new JwtTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}