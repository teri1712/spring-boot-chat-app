package com.decade.practice.infra.bootstrap;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.Instant;

@Configuration
@Order(2)
public class PresencesInitializer implements ApplicationRunner {

        private final UserRepository userRepo;
        private final UserPresenceService onlineStat;

        public PresencesInitializer(UserRepository userRepo, UserPresenceService onlineStat) {
                this.userRepo = userRepo;
                this.onlineStat = onlineStat;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
                User nami = userRepo.findByUsername("Nami");
                User chopper = userRepo.findByUsername("Chopper");
                User zoro = userRepo.findByUsername("Zoro");

                onlineStat.set(nami, Instant.now().getEpochSecond() - 2 * 60);
                onlineStat.set(chopper, Instant.now().getEpochSecond() - 10 * 60);
                onlineStat.set(zoro, Instant.now().getEpochSecond());
        }

}
