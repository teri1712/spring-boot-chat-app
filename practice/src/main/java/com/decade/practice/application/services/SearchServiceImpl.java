package com.decade.practice.application.services;

import com.decade.practice.application.usecases.SearchService;
import com.decade.practice.application.usecases.SearchStore;
import com.decade.practice.dto.MessageHistoryDto;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.events.UserCreatedEvent;
import com.decade.practice.persistence.elastic.MessageDocument;
import com.decade.practice.persistence.elastic.UserDocument;
import com.decade.practice.persistence.elastic.repositories.MessageDocumentRepository;
import com.decade.practice.persistence.elastic.repositories.UserDocumentRepository;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService, SearchStore {

    private final MessageDocumentRepository messageDocumentRepository;
    private final UserDocumentRepository userDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<UserResponse> searchUsers(String string) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.term(t -> t.field("gender").value(User.FEMALE)))
                        .should(m -> m.match(mm -> mm.field("username").query(string)))
                        .should(m -> m.match(mm -> mm.field("name").query(string)))
                        .minimumShouldMatch("1")
                ))
                .build();
        SearchHits<UserDocument> hits =
                elasticsearchOperations.search(query, UserDocument.class);
        return hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(document -> UserResponse.builder()
                        .id(document.getId())
                        .username(document.getUsername())
                        .name(document.getName())
                        .avatar(document.getAvatar())
                        .gender(document.getGender())
                        .build()).toList();
    }

    @Override
    public List<MessageHistoryDto> searchMessages(UUID owner, String string) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.term(t -> t.field("owner").value(owner.toString())))
                        .should(m -> m.match(mm -> mm.field("partnerName").query(string)))
                        .should(m -> m.match(mm -> mm.field("content").query(string).fuzziness("AUTO")))
                        .minimumShouldMatch("1")
                ))
                .build();
        SearchHits<MessageDocument> hits =
                elasticsearchOperations.search(query, MessageDocument.class);

        return hits.getSearchHits().stream().map(SearchHit::getContent)
                .map(document -> MessageHistoryDto.builder()
                        .id(document.getId())
                        .content(document.getContent())
                        .chatIdentifier(document.getChatIdentifier())
                        .partnerName(document.getPartnerName())
                        .build())

                .toList();
    }

    @Override
    public void save(UserCreatedEvent userCreatedEvent) {

        UserDocument document = new UserDocument();
        document.setAvatar(userCreatedEvent.getAvatar());
        document.setUsername(userCreatedEvent.getUsername());
        document.setName(userCreatedEvent.getName());
        document.setGender(userCreatedEvent.getGender());
        document.setDob(userCreatedEvent.getDob());
        document.setId(userCreatedEvent.getUserId());
        userDocumentRepository.save(document);

    }

    @Override
    public void save(MessageCreatedEvent messageCreatedEvent) {
        MessageDocument document = new MessageDocument();
        document.setId(messageCreatedEvent.getIdempotencyKey());
        document.setOwner(messageCreatedEvent.getChat().getOwner());
        document.setChatIdentifier(messageCreatedEvent.getChat().getIdentifier());
        document.setPartnerName(messageCreatedEvent.getPartner().getName());
        document.setContent(messageCreatedEvent.getTextEvent().getContent());


        messageDocumentRepository.save(document);

    }
}
