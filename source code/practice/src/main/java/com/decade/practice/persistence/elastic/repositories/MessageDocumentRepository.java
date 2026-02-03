package com.decade.practice.persistence.elastic.repositories;

import com.decade.practice.persistence.elastic.MessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, UUID> {
}
