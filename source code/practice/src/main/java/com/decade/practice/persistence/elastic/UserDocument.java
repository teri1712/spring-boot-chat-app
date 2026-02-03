package com.decade.practice.persistence.elastic;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;
import java.util.UUID;

@Document(indexName = "users", createIndex = true)
@Getter
@Setter
public class UserDocument {

    @Id
    private UUID id;
    private String username;
    private String name;
    private Date dob;
    private ImageSpec avatar;
    private Float gender;

}
