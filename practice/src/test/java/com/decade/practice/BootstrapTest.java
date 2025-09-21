package com.decade.practice;


import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.DefaultAvatar;
import com.decade.practice.utils.PrerequisiteBeans;
import com.decade.practice.utils.RedisTestContainerSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@AutoConfigureTestDatabase(
        connection = EmbeddedDatabaseConnection.H2
) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BootstrapTest extends RedisTestContainerSupport {

        @Autowired
        private UserService userService;


        @Test
        public void testBootstrappingApplication() {
                userService.create("first", "first", "first", new Date(), "Be de", DefaultAvatar.getInstance(), true);
        }
}
