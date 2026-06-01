package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.MessageHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageHistoryRepository extends CrudRepository<MessageHistory, Long> {

    @Query(value = """
        select *
                from message_history m
                where search_vector @@ websearch_to_tsquery('english', :content)
                and m.chat_id = :chatId
                limit 20
        """)
    List<MessageHistory> findByChatIdAndContent(String chatId, String content);
}
