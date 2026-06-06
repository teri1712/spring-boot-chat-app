package com.decade.practice.engagement.application.ports.in;

import java.util.Set;
import java.util.UUID;

public interface EngagementService {

    void add(String chatId, Set<UUID> partners);


}
