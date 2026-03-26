package com.decade.practice.chatsettings.dto;


import com.decade.practice.chatsettings.api.PreferenceInfo;
import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreferenceMapper {

      @Mapping(source = "preference.theme.background", target = "themeBackground")
      @Mapping(source = "preference.theme.name", target = "themeName")
      PreferenceInfo map(Preference preference);

      PreferenceMessage map(PreferenceChanged preference);
}
