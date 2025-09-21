package com.decade.practice.usecases;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.adapter.repositories.JpaAdminRepository;
import com.decade.practice.adapter.security.jwt.JwtService;
import com.decade.practice.application.services.ChatEventStore;
import com.decade.practice.application.services.ChatServiceImpl;
import com.decade.practice.application.services.UserEventStore;
import com.decade.practice.application.services.UserServiceImpl;
import com.decade.practice.application.services.EventServiceImpl;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.repositories.ChatRepository;
import com.decade.practice.domain.repositories.EdgeRepository;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.utils.PrerequisiteBeans;
import com.decade.practice.utils.RedisTestContainerSupport;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Date;
import java.util.List;

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({
        RedisAutoConfiguration.class,
        JwtService.class,
        ChatServiceImpl.class,
        ChatEventStore.class,
        UserEventStore.class,
        UserServiceImpl.class,
        EventServiceImpl.class,
        PrerequisiteBeans.class
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConversationTest extends RedisTestContainerSupport {

        @Autowired
        private EventStore eventStore;

        @Autowired

        private EdgeRepository edgeRepo;

        @Autowired
        private UserRepository userRepo;

        @Autowired
        private UserService userService;

        @Autowired
        private TransactionOperations template;

        @Autowired
        private ChatRepository chatRepo;

        @Autowired
        private ChatService chatService;
        @Autowired
        private JpaAdminRepository adminRepo;

        private ChatEvent sendEvent(User from, User to, String message) {
                Chat chat = new Chat(from, to);
                TextEvent event = new TextEvent(chat, from, message);
                eventStore.save(event);
                return event;
        }

        private User first;
        private User second;
        private User third;

        @BeforeAll
        public void setUp() {
                template.executeWithoutResult(status -> {
                        adminRepo.save(new Admin("admin", "admin"));
                        adminRepo.flush();
                        first = userService.create("first", "first", "first", new Date(), "male", null, true);
                        second = userService.create("second", "second", "second", new Date(), "male", null, true);
                        third = userService.create("third", "third", "third", new Date(), "male", null, true);
                });
        }

        @BeforeEach
        public void prepare() {
                first = userRepo.findByUsername(first.getUsername());
                second = userRepo.findByUsername(second.getUsername());
                third = userRepo.findByUsername(third.getUsername());
        }

        @Test
        public void testSendEventUpdatesChatStats() {
                ChatEvent event = sendEvent(first, second, "Hello");

                Assertions.assertEquals(5, edgeRepo.count());

                Chat chat = chatRepo.findById(event.getChatIdentifier()).orElseThrow();
                Assertions.assertEquals(1, chat.getFirstUser().getSyncContext().getEventVersion());
                Assertions.assertEquals(1, chat.getSecondUser().getSyncContext().getEventVersion());
                Assertions.assertEquals(1, chat.getMessageCount());
        }

        @Test
        public void testListChatsByLatestActivity() {
                Assertions.assertEquals(3, chatRepo.count());

                Chat secondChat = sendEvent(first, second, "Hello").getChat();
                Assertions.assertEquals(1, first.getSyncContext().getEventVersion());
                Assertions.assertEquals(secondChat.getIdentifier(), edgeRepo.findHeadEdge(first, 1).getFrom().getIdentifier());
                Chat thirdChat = sendEvent(third, first, "I'm fine").getChat();
                Assertions.assertEquals(2, first.getSyncContext().getEventVersion());
                Assertions.assertEquals(thirdChat.getIdentifier(), edgeRepo.findHeadEdge(first, 2).getFrom().getIdentifier());

                Assertions.assertEquals(3 + 2, chatRepo.count());

                Assertions.assertEquals(1 + 2, edgeRepo.findByOwner(first).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size());
                Assertions.assertEquals(2, first.getSyncContext().getEventVersion());
                Assertions.assertEquals(1, second.getSyncContext().getEventVersion());
                Assertions.assertEquals(1, third.getSyncContext().getEventVersion());
                List<Chat> chats = chatService.listChat(first);

                Assertions.assertEquals(1 + 2, chats.size());
                Assertions.assertEquals(second, inspectPartner(chats.get(1), first));
                Assertions.assertEquals(third, inspectPartner(chats.get(0), first));

                Assertions.assertEquals(3 + 2 + 1 + 1, edgeRepo.count());

                sendEvent(second, first, "How are you");
                Assertions.assertEquals(3, first.getSyncContext().getEventVersion());
                Assertions.assertEquals(secondChat.getIdentifier(), edgeRepo.findHeadEdge(first, 3).getFrom().getIdentifier());
                Assertions.assertEquals(3 + 2 + 1 + 1 + 2, edgeRepo.count());

                chats = chatService.listChat(first);

                Assertions.assertEquals(third, inspectPartner(chats.get(1), first));
                Assertions.assertEquals(second, inspectPartner(chats.get(0), first));
                Assertions.assertEquals(1 + 2, chats.size());

                Assertions.assertEquals(3 + 2, chatRepo.count());
                Assertions.assertEquals(1 + 2 + 2, edgeRepo.findByOwner(first).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size());


                Assertions.assertEquals(3 + 2, chatRepo.count());
                Assertions.assertEquals(1 + 2 + 2, edgeRepo.findByOwner(first).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size());
                Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size());
        }


        // Helper method to replace the Kotlin extension function inspectPartner
        private User inspectPartner(Chat chat, User user) {
                if (chat.getFirstUser().equals(user)) {
                        return chat.getSecondUser();
                } else {
                        return chat.getFirstUser();
                }
        }
}