package com.decade.practice.engagement.api;

import java.util.UUID;

public interface DirectMappingApi {

      DirectMapping findDirectMapping(UUID userId, UUID partnerId);
}
