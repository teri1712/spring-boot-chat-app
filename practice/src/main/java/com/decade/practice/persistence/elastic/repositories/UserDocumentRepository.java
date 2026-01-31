package com.decade.practice.persistence.elastic.repositories;

import com.decade.practice.persistence.elastic.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, UUID> {

    List<UserDocument> findByUsernameAndNameContaining(String username, String name);
}
