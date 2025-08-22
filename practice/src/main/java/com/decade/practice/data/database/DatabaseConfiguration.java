package com.decade.practice.data.database;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.decade.practice.model")
@EnableJpaRepositories(basePackages = {"com.decade.practice.data.repositories"})
public class DatabaseConfiguration {

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

        @Bean
        DatabaseInitializer databaseInitializer() {
                return new DatabaseInitializer();
        }


}