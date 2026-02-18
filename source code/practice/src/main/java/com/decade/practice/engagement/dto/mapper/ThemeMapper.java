package com.decade.practice.engagement.dto.mapper;

import com.decade.practice.engagement.domain.Theme;
import com.decade.practice.engagement.dto.ThemeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThemeMapper {

    ThemeResponse themeToResponse(Theme theme);
}
