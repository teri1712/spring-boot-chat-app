package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, UUID> {

}
