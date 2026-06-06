package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.api.DirectMapping;
import com.decade.practice.engagement.api.DirectMappingApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Primary
@ConditionalOnProperty(name = "redis.cache.enabled", havingValue = "true")
public class CacheableDirectMapping implements DirectMappingApi {

    private final DirectMappingApi directMappingApi;

    public CacheableDirectMapping(@Qualifier("defaultDirectMappingApi") DirectMappingApi directMappingApi) {
        this.directMappingApi = directMappingApi;
    }

    @Override
    @Cacheable(cacheNames = "directMapping", key = "#userId + '_' + #partnerId", unless = "#result == null")
    public DirectMapping findDirectMapping(UUID userId, UUID partnerId) {
        return directMappingApi.findDirectMapping(userId, partnerId);
    }
}
