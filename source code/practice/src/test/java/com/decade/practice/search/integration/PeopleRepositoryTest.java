package com.decade.practice.search.integration;

import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import com.decade.practice.search.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PeopleRepositoryTest extends BaseTestClass {
    @Autowired
    PeopleRepository people;

    @Test
    void givenAliceAndBobExist_findAlice_thenMustReturnAliceOnly() {
        people.save(new Person(null, UUID.randomUUID(), "alice", "alice", "Male", "alice.jpg"));
        people.save(new Person(null, UUID.randomUUID(), "bob", "bob", "Male", "bob.jpg"));


        assertThat(people.findPeople("alice")).hasSize(1)
            .extracting(Person::name).contains("alice");
    }
}
