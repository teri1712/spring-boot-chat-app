package com.decade.practice.search.integration;

import com.decade.practice.common.TestDataset;
import com.decade.practice.search.application.ports.out.HistoryRepository;
import com.decade.practice.search.application.ports.out.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SearchDataset implements TestDataset {
    final PeopleRepository people;
    final HistoryRepository histories;

    @Override
    public void clean() {
        people.deleteAll();
        histories.deleteAll();
    }
}
