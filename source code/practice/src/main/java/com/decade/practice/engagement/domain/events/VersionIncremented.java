package com.decade.practice.engagement.domain.events;

import java.util.UUID;

public record VersionIncremented(
          UUID eventId,
          String chatId,
          Integer eventVersion) {


}
