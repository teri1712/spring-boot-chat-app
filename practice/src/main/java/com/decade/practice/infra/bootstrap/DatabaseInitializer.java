package com.decade.practice.infra.bootstrap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DatabaseInitializer implements ApplicationContextAware, ApplicationRunner {

        private ApplicationContext applicationContext;

        @Autowired(required = false)
        private Migrater migrater;

        @Autowired
        private Seeder seeder;


        @Override
        public void run(ApplicationArguments args) throws Exception {
                try {
                        if (migrater != null)
                                migrater.run();
                        seeder.run();
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
