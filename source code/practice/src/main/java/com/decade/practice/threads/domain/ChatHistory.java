package com.decade.practice.threads.domain;

import com.decade.practice.threads.api.events.ChatHistoryCreated;
import com.decade.practice.threads.api.events.HistoryMessageAdded;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class ChatHistory extends AbstractAggregateRoot<ChatHistory> {

    @EmbeddedId
    private ChatHistoryId chatHistoryId;

    private String roomName;
    private String roomAvatar;

    @Version
    private Integer version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Message> messages;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<UUID> seenBy;


    private Instant modifiedAt;

    @Embedded
    private HashValue hash;

    protected ChatHistory() {
    }

    public ChatHistory(String chatId, UUID ownerId, String roomName, String roomAvatar) {
        this.chatHistoryId = new ChatHistoryId(chatId, ownerId);
        this.messages = new ArrayList<>();
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.hash = new HashValue(0L);
        registerEvent(new ChatHistoryCreated(chatId, ownerId, roomName));
    }

    public void addMessage(Message message) {
        this.messages.add(0, message);
        if (this.messages.size() > 100) {
            this.messages.remove(this.messages.size() - 1);
        }
        this.modifiedAt = message.createdAt();
        this.hash = hash.plus(message.computeHash());
        this.seenBy = new ArrayList<>();
        registerEvent(new HistoryMessageAdded(
                chatHistoryId.chatId(),
                roomName,
                chatHistoryId.ownerId(),
                message.content(),
                message.createdAt()));
    }


    public void update(String roomName, String roomAvatar) {
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
    }

    public void addSeenBy(UUID seenBy) {
        this.seenBy.add(seenBy);
    }


}
