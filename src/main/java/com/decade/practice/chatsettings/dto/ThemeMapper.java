package com.decade.practice.chatsettings.dto;

import com.decade.practice.chatsettings.domain.Theme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThemeMapper {

      @Mapping(source = "name", target = "themeName")
      ThemeResponse map(Theme theme);
}
