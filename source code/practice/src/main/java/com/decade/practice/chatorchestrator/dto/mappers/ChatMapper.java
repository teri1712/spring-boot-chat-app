package com.decade.practice.chatorchestrator.dto.mappers;


import com.decade.practice.chatorchestrator.dto.ChatResponse;
import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.engagement.api.ChatPolicyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PreferenceInfoMapper.class})
public interface ChatMapper {
      ChatResponse map(ChatPolicyInfo policy, SettingsInfo settings);
}
