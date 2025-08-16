package com.decade.practice.usecases;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.data.database.DatabaseConfiguration;
import com.decade.practice.data.repositories.ChatRepository;
import com.decade.practice.data.repositories.EdgeRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.TextEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.utils.PrerequisiteBeans;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
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
        JwtCredentialService.class,
        ChatService.class,
        ChatEventStore.class,
        UserEventStore.class,
        UserService.class,
        DatabaseConfiguration.class,
        PrerequisiteBeans.class
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConversationTest {

    @Autowired
    private EventStore eventStore;

    @Autowired
    private EdgeRepository edgeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserOperations userOperations;

    @Autowired
    private TransactionOperations template;

    @Autowired
    private ChatRepository chatRepo;

    @Autowired
    private ChatOperations chatOperations;

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
            first = userOperations.create("first", "first", "first", new Date(), "male", null, true);
            second = userOperations.create("second", "second", "second", new Date(), "male", null, true);
            third = userOperations.create("third", "third", "third", new Date(), "male", null, true);
        });
    }

    @BeforeEach
    public void prepare() {
        first = userRepo.getByUsername(first.getUsername());
        second = userRepo.getByUsername(second.getUsername());
        third = userRepo.getByUsername(third.getUsername());
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
        Assertions.assertEquals(secondChat.getIdentifier(), edgeRepo.getHeadEdge(first, 1).getFrom().getIdentifier());
        Chat thirdChat = sendEvent(third, first, "I'm fine").getChat();
        Assertions.assertEquals(2, first.getSyncContext().getEventVersion());
        Assertions.assertEquals(thirdChat.getIdentifier(), edgeRepo.getHeadEdge(first, 2).getFrom().getIdentifier());

        Assertions.assertEquals(3 + 2, chatRepo.count());

        Assertions.assertEquals(1 + 2, edgeRepo.findByOwner(first).size());
        Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(second).size());
        Assertions.assertEquals(1 + 1, edgeRepo.findByOwner(third).size());
        Assertions.assertEquals(2, first.getSyncContext().getEventVersion());
        Assertions.assertEquals(1, second.getSyncContext().getEventVersion());
        Assertions.assertEquals(1, third.getSyncContext().getEventVersion());
        List<Chat> chats = chatOperations.listChat(first);

        Assertions.assertEquals(1 + 2, chats.size());
        Assertions.assertEquals(second, inspectPartner(chats.get(1), first));
        Assertions.assertEquals(third, inspectPartner(chats.get(0), first));

        Assertions.assertEquals(3 + 2 + 1 + 1, edgeRepo.count());

        sendEvent(second, first, "How are you");
        Assertions.assertEquals(3, first.getSyncContext().getEventVersion());
        Assertions.assertEquals(secondChat.getIdentifier(), edgeRepo.getHeadEdge(first, 3).getFrom().getIdentifier());
        Assertions.assertEquals(3 + 2 + 1 + 1 + 2, edgeRepo.count());

        chats = chatOperations.listChat(first);

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

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @AfterAll
    public void tearDown() {
        redisTemplate.execute((RedisConnection conn) -> {
            conn.flushDb();
            return null;
        });
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