package com.decade.practice.common;

import com.decade.practice.search.application.ports.out.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class PeopleCleanUp implements DataCleanUp {
    private final PeopleRepository people;

    @Override
    public void clean() {
        people.deleteAll();
    }
}
