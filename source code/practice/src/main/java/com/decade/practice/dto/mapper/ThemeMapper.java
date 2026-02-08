package com.decade.practice.dto.mapper;

import com.decade.practice.dto.ThemeResponse;
import com.decade.practice.persistence.jpa.entities.Theme;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThemeMapper {

    ThemeResponse toThemeResponse(Theme theme);
}
