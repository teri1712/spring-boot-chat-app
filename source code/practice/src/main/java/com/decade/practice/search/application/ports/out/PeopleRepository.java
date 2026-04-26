package com.decade.practice.search.application.ports.out;

import com.decade.practice.search.domain.Person;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeopleRepository extends CrudRepository<Person, Long> {

    @Query(value = """
        select *  from people
        where search_vector @@ websearch_to_tsquery('english', name)
        limit 20
        """)
    List<Person> findPeople(String name);

    Optional<Person> findByUserId(UUID userId);
}
