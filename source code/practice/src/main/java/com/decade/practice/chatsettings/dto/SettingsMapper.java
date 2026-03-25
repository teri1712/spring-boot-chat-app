package com.decade.practice.chatsettings.dto;


import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.chatsettings.domain.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PreferenceMapper.class})
public interface SettingsMapper {

      @Mapping(source = "setting.identifier", target = "id")
      SettingsInfo map(Setting setting);
}
