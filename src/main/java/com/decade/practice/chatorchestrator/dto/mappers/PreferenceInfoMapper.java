package com.decade.practice.chatorchestrator.dto.mappers;


import com.decade.practice.chatorchestrator.dto.PreferenceResponse;
import com.decade.practice.chatsettings.api.PreferenceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreferenceInfoMapper {

      PreferenceResponse map(PreferenceInfo preference);
}
