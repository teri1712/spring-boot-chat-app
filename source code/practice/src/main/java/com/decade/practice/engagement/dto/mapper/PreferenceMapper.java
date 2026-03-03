package com.decade.practice.engagement.dto.mapper;


import com.decade.practice.engagement.domain.Preference;
import com.decade.practice.engagement.dto.PreferenceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreferenceMapper {

      PreferenceResponse map(Preference preference);
}
