package com.decade.practice.presence;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.entity.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

@Configuration
@Order(2)
public class PresencesInitializer implements ApplicationRunner {

        private final StringRedisTemplate redisTemplate;
        private final UserRepository userRepo;
        private final UserPresenceService onlineStat;

        public PresencesInitializer(StringRedisTemplate redisTemplate, UserRepository userRepo, UserPresenceService onlineStat) {
                this.redisTemplate = redisTemplate;
                this.userRepo = userRepo;
                this.onlineStat = onlineStat;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
                redisTemplate.delete(redisTemplate.keys("*"));
                User nami = userRepo.getByUsername("Nami");
                User chopper = userRepo.getByUsername("Chopper");
                User zoro = userRepo.getByUsername("Zoro");

                onlineStat.set(nami, Instant.now().getEpochSecond() - 2 * 60);
                onlineStat.set(chopper, Instant.now().getEpochSecond() - 10 * 60);
                onlineStat.set(zoro, Instant.now().getEpochSecond());
        }

}
