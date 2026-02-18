package com.decade.practice.presence.application.query;

import com.decade.practice.presence.dto.PresenceResponse;

import java.util.List;

public interface PresenceService {

    PresenceResponse get(String username);

    List<PresenceResponse> getOnlineList(String username);
}