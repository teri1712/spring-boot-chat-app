package com.decade.practice.usecases;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.adapter.security.jwt.JwtService;
import com.decade.practice.application.services.ChatEventStore;
import com.decade.practice.application.services.ChatServiceImpl;
import com.decade.practice.application.services.UserEventStore;
import com.decade.practice.application.services.UserServiceImpl;
import com.decade.practice.application.services.EventServiceImpl;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.entities.User;
import com.decade.practice.infra.configs.DatabaseConfiguration;
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
        JwtService.class,
        ChatServiceImpl.class,
        ChatEventStore.class,
        UserEventStore.class,
        UserServiceImpl.class,
        EventServiceImpl.class,
        DatabaseConfiguration.class
})
public class ChatServiceTest extends RedisTestContainerSupport {

        @Autowired
        private ChatService chatService;

        @Autowired
        private UserService userService;


        private User first;
        private User second;

        @Test
        @Rollback(false)
        @Order(1)
        public void testPrepare() {
                first = userService.create("first", "first", "first", new Date(), "male", null, true);
                second = userService.create("second", "second", "second", new Date(), "male", null, true);
        }

        @Test
        @Order(2)
        public void testGetOrCreateChat() {
                var chat = chatService.getOrCreateChat(first.getId(), second.getId());
                Assertions.assertNotNull(chat.getInteractTime());
        }


}