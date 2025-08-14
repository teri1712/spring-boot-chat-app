package com.decade.practice.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class PrerequisiteBeans {

        @Bean
        public PasswordEncoder encoder() {
                return new BCryptPasswordEncoder();
        }
}
