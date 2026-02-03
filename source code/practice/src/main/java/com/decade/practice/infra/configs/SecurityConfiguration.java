package com.decade.practice.infra.configs;

import com.decade.practice.infra.security.jwt.JwtTokenFilter;
import com.decade.practice.infra.security.strategies.EntryPointStrategy;
import com.decade.practice.infra.security.strategies.LoginFailStrategy;
import com.decade.practice.infra.security.strategies.LoginSuccessStrategy;
import com.decade.practice.infra.security.strategies.LogoutStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
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

    @Value("${actuator.user.name}")
    private String actuatorUsername;

    @Value("${actuator.user.password}")
    private String actuatorPassword;

    @Value("${actuator.user.roles}")
    private String actuatorRoles;

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
    public SecurityContextRepository contextRepository() {
        return new RequestAttributeSecurityContextRepository();
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
    @Order(0)
    public SecurityFilterChain actuatorSecurity(HttpSecurity http) throws Exception {
        UserDetails actuator = User.builder()
                .username(actuatorUsername)
                .password(passwordEncoder().encode(actuatorPassword))
                .roles(actuatorRoles.split(","))
                .build();
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider();
        adminProvider.setUserDetailsService(new InMemoryUserDetailsManager(actuator));
        adminProvider.setPasswordEncoder(passwordEncoder());

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
                .csrf(csrf -> csrf.disable());
        return http.build();
    }


    @Bean(name = FILTER_CHAIN_BEAN_NAME)
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            LoginSuccessStrategy strategy,
            LogoutStrategy logoutHandler,
            JwtTokenFilter jwtAuthenticationFilter
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
                        authorize.requestMatchers("/files/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers("/tokens/**").permitAll()
                                .requestMatchers(WebSocketConfiguration.HANDSHAKE_DESTINATION).permitAll()
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
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