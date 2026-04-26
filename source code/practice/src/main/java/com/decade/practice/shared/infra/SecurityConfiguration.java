package com.decade.practice.shared.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${actuator.user.name}")
    private String actuatorUsername;

    @Value("${actuator.user.password}")
    private String actuatorPassword;

    @Value("${actuator.user.roles}")
    private String actuatorRoles;

    @Bean
    @Order(0)
    public SecurityFilterChain actuatorSecurity(PasswordEncoder passwordEncoder, HttpSecurity http) throws Exception {
        UserDetails actuator = User.builder()
            .username(actuatorUsername)
            .password(passwordEncoder.encode(actuatorPassword))
            .roles(actuatorRoles.split(","))
            .build();
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider(new InMemoryUserDetailsManager(actuator));
        adminProvider.setPasswordEncoder(passwordEncoder);

        http
            .authenticationManager(new ProviderManager(adminProvider))
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(
                auth -> auth
                    .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                    .requestMatchers(EndpointRequest.to("prometheus")).hasRole("OPS")
                    .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }


    @Bean
    public SecurityFilterChain apiDocChain(
        HttpSecurity http
    ) throws Exception {
        http
            .securityMatcher("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .requestCache(Customizer.withDefaults())
            .securityContext(context ->
                context.securityContextRepository(new RequestAttributeSecurityContextRepository())
            )
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().permitAll()
            );
        return http.build();
    }
}