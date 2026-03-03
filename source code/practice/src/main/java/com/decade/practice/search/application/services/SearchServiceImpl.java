package com.decade.practice.search.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.search.application.queries.SearchService;
import com.decade.practice.search.domain.MessageDocument;
import com.decade.practice.search.domain.UserDocument;
import com.decade.practice.search.dto.MatchingMessageHistoryResponse;
import com.decade.practice.search.dto.MatchingUserResponse;
import com.decade.practice.users.api.UserApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

      private final ElasticsearchOperations elasticsearchOperations;
      private final UserApi userApi;

      @Override
      public List<MatchingUserResponse> searchUsers(String string) {
            Query query = NativeQuery.builder()
                      .withQuery(q -> q.bool(b -> b
                                .should(f -> f.term(t -> t.field("gender").value("Female")))
                                .should(m -> m.match(mm -> mm.field("username").query(string)))
                                .should(m -> m.match(mm -> mm.field("name").query(string)))
                                .minimumShouldMatch("1")
                      ))
                      .withHighlightQuery(new HighlightQuery(
                                new Highlight(List.of(
                                          new HighlightField("username",
                                                    HighlightFieldParameters.builder()
                                                              .withPreTags("<strong>")
                                                              .withPostTags("</strong>")
                                                              .build()
                                          ),
                                          new HighlightField("name",
                                                    HighlightFieldParameters.builder()
                                                              .withPreTags("<strong>")
                                                              .withPostTags("</strong>")
                                                              .build()
                                          )
                                )),
                                null
                      ))
                      .build();
            SearchHits<UserDocument> hits =
                      elasticsearchOperations.search(query, UserDocument.class);
            return hits.getSearchHits()
                      .stream()
                      .map(SearchHit::getContent)
                      .map(document -> new MatchingUserResponse(
                                document.getId(),
                                document.getName(),
                                document.getAvatar()
                      )).toList();
      }

      @Override
      @ReadPolicy
      public List<MatchingMessageHistoryResponse> searchMessages(String chatId, UUID userId, String string) {
            Query query = NativeQuery.builder()
                      .withQuery(q -> q.bool(b -> b
                                .filter(f -> f.term(t -> t.field("chatId").value(chatId)))
                                .should(m -> m.match(mm -> mm.field("content").query(string).fuzziness("AUTO")))
                                .should(m -> m.match(mm -> mm.field("chatRoomName").query(string).fuzziness("AUTO")))
                                .minimumShouldMatch("1")
                      ))
                      .withHighlightQuery(new HighlightQuery(
                                new Highlight(List.of(
                                          new HighlightField("chatRoomName",
                                                    HighlightFieldParameters.builder()
                                                              .withPreTags("<strong>")
                                                              .withPostTags("</strong>")
                                                              .build()
                                          ),
                                          new HighlightField("content",
                                                    HighlightFieldParameters.builder()
                                                              .withPreTags("<strong>")
                                                              .withPostTags("</strong>")
                                                              .build()
                                          )
                                )),
                                null
                      ))
                      .build();
            SearchHits<MessageDocument> hits =
                      elasticsearchOperations.search(query, MessageDocument.class);

            return hits.getSearchHits().stream().map(SearchHit::getContent)
                      .map(document -> MatchingMessageHistoryResponse.builder()
                                .content(document.getContent())
                                .chatId(document.getChatId())
                                .roomName(document.getChatRoomName())
                                .creators(
                                          userApi.getUserInfo(new HashSet<>(document.getChatCreators()))
                                                    .values().stream().toList())
                                .build())

                      .toList();
      }

}
