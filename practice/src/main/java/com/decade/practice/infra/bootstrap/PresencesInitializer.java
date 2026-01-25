package com.decade.practice.infra.bootstrap;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.infra.security.jwt.JwtUser;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class PresencesInitializer implements ApplicationRunner {

    private final UserPresenceService presenceService;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.findByUsername("Nami").ifPresent(new Consumer<User>() {
            @Override
            public void accept(User user) {
                JwtUser jwtUser = new JwtUser(UserClaims.from(user));
                presenceService.set(jwtUser, Instant.ofEpochSecond(Instant.now().getEpochSecond() - 2 * 60));
            }
        });
        userRepository.findByUsername("Chopper").ifPresent(new Consumer<User>() {
            @Override
            public void accept(User user) {
                JwtUser jwtUser = new JwtUser(UserClaims.from(user));
                presenceService.set(jwtUser, Instant.ofEpochSecond(Instant.now().getEpochSecond() - 10 * 60));
            }
        });
        userRepository.findByUsername("Zoro").ifPresent(new Consumer<User>() {
            @Override
            public void accept(User user) {
                JwtUser jwtUser = new JwtUser(UserClaims.from(user));
                presenceService.set(jwtUser, Instant.ofEpochSecond(Instant.now().getEpochSecond()));
            }
        });
    }

}
