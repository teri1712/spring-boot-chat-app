package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.MessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, UUID> {
}
