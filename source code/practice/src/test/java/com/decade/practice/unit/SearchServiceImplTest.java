package com.decade.practice.unit;

import com.decade.practice.application.services.SearchServiceImpl;
import com.decade.practice.dto.ChatDto;
import com.decade.practice.dto.MessageHistoryDto;
import com.decade.practice.dto.TextEventDto;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.events.UserCreatedEvent;
import com.decade.practice.persistence.elastic.MessageDocument;
import com.decade.practice.persistence.elastic.UserDocument;
import com.decade.practice.persistence.elastic.repositories.MessageDocumentRepository;
import com.decade.practice.persistence.elastic.repositories.UserDocumentRepository;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private MessageDocumentRepository messageDocumentRepository;
    @Mock
    private UserDocumentRepository userDocumentRepository;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void givenQuery_whenSearchUsers_thenReturnsUserList() {
        String queryStr = "test";
        UserDocument doc = new UserDocument();
        doc.setId(UUID.randomUUID());
        doc.setUsername("testuser");
        doc.setName("Test User");
        doc.setGender(User.FEMALE);

        SearchHit<UserDocument> hit = mock(SearchHit.class);
        given(hit.getContent()).willReturn(doc);
        SearchHits<UserDocument> hits = mock(SearchHits.class);
        given(hits.getSearchHits()).willReturn(List.of(hit));

        given(elasticsearchOperations.search(any(Query.class), eq(UserDocument.class))).willReturn(hits);

        List<UserResponse> result = searchService.searchUsers(queryStr);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void givenOwnerAndQuery_whenSearchMessages_thenReturnsMessageList() {
        UUID ownerId = UUID.randomUUID();
        String queryStr = "hello";
        MessageDocument doc = new MessageDocument();
        doc.setId(UUID.randomUUID());
        doc.setContent("hello world");
        doc.setPartnerName("Partner");

        SearchHit<MessageDocument> hit = mock(SearchHit.class);
        given(hit.getContent()).willReturn(doc);
        SearchHits<MessageDocument> hits = mock(SearchHits.class);
        given(hits.getSearchHits()).willReturn(List.of(hit));

        given(elasticsearchOperations.search(any(Query.class), eq(MessageDocument.class))).willReturn(hits);

        List<MessageHistoryDto> result = searchService.searchMessages(ownerId, queryStr);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hello world", result.get(0).getContent());
    }

    @Test
    void givenUserCreatedEvent_whenSave_thenRepositorySaveIsCalled() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(UUID.randomUUID())
                .username("newuser")
                .name("New User")
                .gender(User.MALE)
                .build();

        searchService.save(event);

        verify(userDocumentRepository).save(any(UserDocument.class));
    }

    @Test
    void givenMessageCreatedEvent_whenSave_thenRepositorySaveIsCalled() {
        MessageCreatedEvent event = MessageCreatedEvent.builder()
                .idempotencyKey(UUID.randomUUID())
                .chat(new ChatDto(new ChatIdentifier(UUID.randomUUID(), UUID.randomUUID()), UUID.randomUUID()))
                .partner(UserResponse.builder().name("Partner").build())
                .textEvent(new TextEventDto("Hello"))
                .build();

        searchService.save(event);

        verify(messageDocumentRepository).save(any(MessageDocument.class));
    }
}
