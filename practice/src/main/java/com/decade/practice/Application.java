package com.decade.practice;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.presence.UserPresenceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;

@SpringBootApplication
public class Application {

        public static void main(String[] args) {
                SpringApplication app = createApp(Application.class);
                ApplicationContext context = app.run(args);
                seeding(context);
        }

        private static boolean isRunningInContainer() {
                return System.getenv().containsKey("DECADE");
        }

        public static SpringApplication createApp(Class<?> appClass) {
                SpringApplication app = new SpringApplication(appClass);
//                System.out.println("isRunningInContainer:" + isRunningInContainer());
//                System.out.println("environments: " + System.getenv().toString() + "\n\n\n");
                if (isRunningInContainer()) {
                        app.setAdditionalProfiles("docker");
                }
                return app;
        }

        public static void seeding(ApplicationContext context) {
                StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
                redisTemplate.delete(redisTemplate.keys("*"));
                UserRepository userRepo = context.getBean(UserRepository.class);
                UserPresenceService onlineStat = context.getBean(UserPresenceService.class);

                User nami = userRepo.getByUsername("Nami");
                User chopper = userRepo.getByUsername("Chopper");

                onlineStat.set(nami, Instant.now().getEpochSecond() - 2 * 60);
                onlineStat.set(chopper, Instant.now().getEpochSecond() - 10 * 60);
        }
}
