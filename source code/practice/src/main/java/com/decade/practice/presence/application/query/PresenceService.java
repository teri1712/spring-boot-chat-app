package com.decade.practice.presence.application.query;

import com.decade.practice.presence.dto.BuddyResponse;
import com.decade.practice.presence.dto.RoomPresenceResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PresenceService {

      Map<String, RoomPresenceResponse> find(UUID caller, Set<String> chatIds);

      List<BuddyResponse> findMyBuddies(UUID userId);
}