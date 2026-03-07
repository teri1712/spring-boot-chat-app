package com.decade.practice.search.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "messages", createIndex = true)
@AllArgsConstructor
@Getter
public class MessageDocument {

      @Id
      private UUID id;

      private String content;

      @Field(type = FieldType.Keyword)
      private String chatId;


      @Field(type = FieldType.Date,
                format = DateFormat.date_time)
      private Instant createdAt;
}
