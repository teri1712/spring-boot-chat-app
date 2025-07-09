package com.decade.practice.database;

import com.decade.practice.database.repository.AdminRepository;
import com.decade.practice.model.domain.entity.Admin;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EntityScan("com.decade.practice.model")
@EnableJpaRepositories(basePackages = {"com.decade.practice.database.repository"})
public class DatabaseConfiguration implements ApplicationContextAware {


      // faster xml + hibernate conflict
      @Bean
      public Module hibernateJacksonModule() {
            return new Hibernate6Module();
      }

      @Value("${admin.username}")
      private String adminUsername;

      @Value("${admin.password}")
      private String adminPassword;

      private ApplicationContext appCtx;

      @EventListener(ApplicationReadyEvent.class)
      public void onApplicationReady() {
            AdminRepository adminRepo = appCtx.getBean(AdminRepository.class);
            if (adminRepo.getOrNull() == null) {
                  adminRepo.save(new Admin(adminUsername, adminPassword));
            }
      }

      @Override
      public void setApplicationContext(ApplicationContext applicationContext) {
            this.appCtx = applicationContext;
      }
}