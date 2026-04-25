package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageDocumentRepository extends CrudRepository<History, UUID> {

    @Query(value = """
        select *, ts_rank(search_vector,websearch_to_tsquery('english',:content)) as rank
                from message_history m
                where search_vector @@ websearch_to_tsquery('english', :content)
                and m.chat_id = :chatId
                order by rank desc
                limit 20
        """, nativeQuery = true)
    List<History> findByChatIdAndContent(String chatId, String content);
}
