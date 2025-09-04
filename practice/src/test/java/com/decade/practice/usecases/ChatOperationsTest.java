package com.decade.practice.usecases;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.data.database.DatabaseConfiguration;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.utils.PrerequisiteBeans;
import com.decade.practice.utils.RedisTestContainerSupport;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@ExtendWith(OutputCaptureExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({
        RedisAutoConfiguration.class,
        JwtCredentialService.class,
        ChatService.class,
        ChatEventStore.class,
        UserEventStore.class,
        UserService.class,
        DatabaseConfiguration.class
})
public class ChatOperationsTest extends RedisTestContainerSupport {

        @Autowired
        private ChatOperations chatOperations;

        @Autowired
        private UserOperations userOperations;


        private User first;
        private User second;

        @Test
        @Rollback(false)
        @Order(1)
        public void testPrepare() {
                first = userOperations.create("first", "first", "first", new Date(), "male", null, true);
                second = userOperations.create("second", "second", "second", new Date(), "male", null, true);
        }

        @Test
        @Order(2)
        public void testGetOrCreateChat() {
                var chat = chatOperations.getOrCreateChat(first.getId(), second.getId());
                Assertions.assertNotNull(chat.getInteractTime());
        }


}