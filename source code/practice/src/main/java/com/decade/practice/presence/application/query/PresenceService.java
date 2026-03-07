package com.decade.practice.presence.application.query;

import com.decade.practice.presence.dto.ChatPresenceResponse;
import com.decade.practice.presence.dto.PresenceRecommendationResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PresenceService {

      Map<String, ChatPresenceResponse> find(UUID caller, Set<String> chatIds);

      List<PresenceRecommendationResponse> findRecommendation(UUID userId);
}