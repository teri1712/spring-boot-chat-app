package com.decade.practice.dto.mapper;


import com.decade.practice.dto.PreferenceResponse;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ThemeMapper.class)
public interface PreferenceMapper {

    PreferenceResponse toPreferenceResponse(Preference preference);
}
