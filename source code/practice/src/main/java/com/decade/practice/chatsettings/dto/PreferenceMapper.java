package com.decade.practice.chatsettings.dto;


import com.decade.practice.chatsettings.api.PreferenceInfo;
import com.decade.practice.chatsettings.domain.Preference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreferenceMapper {

      @Mapping(source = "preference.theme.background", target = "theme")
      PreferenceInfo map(Preference preference);
}
