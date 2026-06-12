package com.decade.practice.presence.application.ports.out;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ScoreEngine {

    void incScore(String collection, String entity);

    double getScore(String collection, String entity);

    List<String> findTopK(String collection, int limit);

    Map<String, List<String>> findTopK(Set<String> collections, int limit);
}
