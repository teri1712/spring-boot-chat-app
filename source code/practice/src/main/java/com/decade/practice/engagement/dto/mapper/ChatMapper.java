package com.decade.practice.engagement.dto.mapper;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.dto.ChatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PreferenceMapper.class})
public interface ChatMapper {

    @Mapping(source = "chat.preference", target = "preference")
    ChatResponse toResponse(Chat chat, Boolean freshOne);

}
