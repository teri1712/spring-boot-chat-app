package com.decade.practice.inbox.adapter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface UserIdsAggregator<T> {
    Stream<UUID> aggregate(T sth);

    default Stream<UUID> aggregate(List<T> sths) {
        return sths.stream().flatMap(this::aggregate);
    }
}
