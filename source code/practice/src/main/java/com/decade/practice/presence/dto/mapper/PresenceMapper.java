package com.decade.practice.presence.dto.mapper;

import com.decade.practice.presence.domain.Presence;
import com.decade.practice.presence.dto.PresenceRecommendationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PresenceMapper {

      PresenceRecommendationResponse map(Presence presence);
}
