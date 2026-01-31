package com.decade.practice.persistence.elastic;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "messages", createIndex = true)
public class MessageDocument {

    @Id
    private UUID id;
    private UUID sender;
    private ChatIdentifier chatIdentifier;

    private String senderName;
    private String roomName;
    private String content;
}
