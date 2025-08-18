package com.decade.practice.data.database;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@Configuration
@EntityScan("com.decade.practice.model")
@EnableJpaRepositories(basePackages = {"com.decade.practice.data.repositories"})
public class DatabaseConfiguration implements ApplicationContextAware, ApplicationRunner {

        private final Migrater migrater;

        public DatabaseConfiguration(Optional<Migrater> migrater) {
                this.migrater = migrater.orElse(null);
        }

        private ApplicationContext applicationContext;

        // faster xml + hibernate conflict
        @Bean
        public Module hibernateJacksonModule() {
                return new Hibernate6Module();
        }

        @Bean
        Seeder seeder() {
                return new Seeder();
        }

        @Bean
        @ConditionalOnProperty(name = "spring.jpa.database", havingValue = "MYSQL")
        Migrater migrater() {
                return new Migrater();
        }


        @Override
        public void run(ApplicationArguments args) throws Exception {
                try {
                        if (migrater != null)
                                migrater.run();
                        seeder().run();
                } catch (Exception e) {
                        e.printStackTrace();
                        int exitCode = SpringApplication.exit(applicationContext, () -> 1);
                        System.exit(exitCode);
                }

        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                this.applicationContext = applicationContext;
        }
}