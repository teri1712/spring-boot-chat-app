package com.decade.practice.persistence.elastic.repositories;

import com.decade.practice.persistence.elastic.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, UUID> {

}
