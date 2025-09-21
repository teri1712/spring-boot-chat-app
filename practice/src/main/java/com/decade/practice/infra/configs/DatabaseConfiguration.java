package com.decade.practice.infra.configs;

import com.decade.practice.infra.bootstrap.DatabaseInitializer;
import com.decade.practice.infra.bootstrap.Migrater;
import com.decade.practice.infra.bootstrap.Seeder;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.decade.practice.adapter.repositories"})
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
        @Order(1)
        DatabaseInitializer databaseInitializer() {
                return new DatabaseInitializer();
        }


}