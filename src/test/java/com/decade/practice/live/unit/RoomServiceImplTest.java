package com.decade.practice.live.unit;

import com.decade.practice.live.application.ports.out.JoinerRepository;
import com.decade.practice.live.application.ports.out.LivenessBroker;
import com.decade.practice.live.application.services.RoomServiceImpl;
import com.decade.practice.live.domain.RoomJoiner;
import com.decade.practice.live.dto.TypeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private LivenessBroker broker;

    @Mock
    private JoinerRepository joiners;

    @InjectMocks
    private RoomServiceImpl roomService;

    private final String roomTopic = "room-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roomService, "roomTopic", roomTopic);
    }

    @Test
    void givenUserAndChat_whenJoin_thenSubAndSave() {
        // Given
        String chatId = "chat-123";
        UUID userId = UUID.randomUUID();
        String avatar = "avatar.png";

        // When
        roomService.join(chatId, userId, avatar);

        // Then
        verify(broker).sub(roomTopic + ":" + chatId);
        
        ArgumentCaptor<RoomJoiner> joinerCaptor = ArgumentCaptor.forClass(RoomJoiner.class);
        verify(joiners).save(joinerCaptor.capture());
        
        RoomJoiner savedJoiner = joinerCaptor.getValue();
        assertThat(savedJoiner.getChatId()).isEqualTo(chatId);
        assertThat(savedJoiner.getUserId()).isEqualTo(userId);
        assertThat(savedJoiner.getAvatar()).isEqualTo(avatar);
    }

    @Test
    void givenUserAndChat_whenLeave_thenUnsubAndDelete() {
        // Given
        String chatId = "chat-123";
        UUID userId = UUID.randomUUID();
        String avatar = "avatar.png";
        String key = RoomJoiner.determineKey(userId, chatId);
        RoomJoiner joiner = new RoomJoiner(chatId, userId, avatar);

        when(joiners.findById(key)).thenReturn(Optional.of(joiner));

        // When
        roomService.leave(chatId, userId, avatar);

        // Then
        verify(broker).unSub(roomTopic + ":" + chatId);
        verify(joiners).delete(joiner);
    }

    @Test
    void givenUserAndChat_whenType_thenSaveAndSend() {
        // Given
        String chatId = "chat-123";
        UUID userId = UUID.randomUUID();
        String avatar = "avatar.png";
        String key = RoomJoiner.determineKey(userId, chatId);
        RoomJoiner joiner = new RoomJoiner(chatId, userId, avatar);

        when(joiners.findById(key)).thenReturn(Optional.of(joiner));

        // When
        roomService.type(chatId, userId, avatar);

        // Then
        verify(joiners).save(joiner);
        
        ArgumentCaptor<TypeMessage> messageCaptor = ArgumentCaptor.forClass(TypeMessage.class);
        verify(broker).send(eq(roomTopic + ":" + chatId), messageCaptor.capture());
        
        TypeMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.chatId()).isEqualTo(chatId);
        assertThat(sentMessage.from()).isEqualTo(userId);
        assertThat(sentMessage.avatar()).isEqualTo(avatar);
        assertThat(sentMessage.time()).isNotNull();
    }
}
