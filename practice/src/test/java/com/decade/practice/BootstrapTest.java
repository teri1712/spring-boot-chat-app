package com.decade.practice;


import com.decade.practice.model.domain.DefaultAvatar;
import com.decade.practice.usecases.UserOperations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@AutoConfigureTestDatabase(
        connection = EmbeddedDatabaseConnection.H2
) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BootstrapTest {

        @Autowired
        private UserOperations userService;

        @Autowired
        private RedisTemplate<Object, Object> redisTemplate;

        @Test
        public void testBootstrappingApplication() {
                userService.create("first", "first", "first", new Date(), "Be de", DefaultAvatar.getInstance(), true);
        }


        @AfterAll
        public void tearDown() {
                redisTemplate.execute((RedisConnection conn) -> {
                        conn.flushDb();
                        return null;
                });
        }
}
