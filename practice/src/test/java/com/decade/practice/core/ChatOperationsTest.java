package com.decade.practice.core;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.database.DatabaseConfiguration;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.usecases.ChatService;
import com.decade.practice.usecases.UserService;
import com.decade.practice.usecases.core.ChatOperations;
import com.decade.practice.usecases.core.UserOperations;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({
        RedisAutoConfiguration.class,
        JwtCredentialService.class,
        ChatService.class,
        UserService.class,
        DatabaseConfiguration.class
})
public class ChatOperationsTest {

        @Autowired
        private ChatOperations chatOperations;

        @Autowired
        private UserOperations userOperations;

        @MockBean
        private PasswordEncoder encoder;

        private User first;
        private User second;

        @Test
        @Rollback(false)
        @Order(1)
        public void prepare() {
                Mockito.when(encoder.encode(Mockito.anyString())).thenAnswer(invocation ->
                        invocation.getArgument(0, String.class)
                );

                first = userOperations.create("first", "first", "first", new Date(), "male", null, true);
                second = userOperations.create("second", "second", "second", new Date(), "male", null, true);
        }

        @Test
        @Order(2)
        public void given_twoUsers_when_getOrCreateChat_then_returnsChatInstance() {
                var chat = chatOperations.getOrCreateChat(first.getId(), second.getId());
                Assertions.assertNotNull(chat.getInteractTime());
        }

        @Autowired
        private RedisTemplate<Object, Object> redisTemplate;

        @AfterAll
        public void tearDown() {
                redisTemplate.execute((RedisConnection conn) -> {
                        conn.flushDb();
                        return null;
                });
        }
}