package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.ChatDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatDocumentRepository extends ElasticsearchRepository<ChatDocument, String> {
}
