package com.decade.practice.persistence.elastic;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Document(indexName = "messages", createIndex = true)
@Setter
@Getter
public class MessageDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Keyword)
    private UUID owner;
    private ChatIdentifier chatIdentifier;

    private String partnerName;
    private String content;
}
