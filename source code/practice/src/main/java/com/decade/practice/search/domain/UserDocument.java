package com.decade.practice.search.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "users", createIndex = true)
@Getter
@AllArgsConstructor
public class UserDocument {

    @Id
    private UUID id;
    private String username;
    private String name;
    private String gender;
    private String avatar;

    protected UserDocument() {
    }
}
