package com.decade.practice.search.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "chats", createIndex = true)
@Getter
public class ChatDocument {

    @Id
    private String id;

    private String chatId;
    private UUID ownerId;

    @Setter
    private String roomName;

    public ChatDocument(String chatId, UUID ownerId, String roomName) {
        this.chatId = chatId;
        this.ownerId = ownerId;
        this.roomName = roomName;
        this.id = getKey(chatId, ownerId);
    }

    protected ChatDocument() {
    }


    public static String getKey(String chatId, UUID ownerId) {
        return chatId + "-" + ownerId;
    }

}
