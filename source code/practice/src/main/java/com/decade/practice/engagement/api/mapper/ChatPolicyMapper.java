package com.decade.practice.engagement.api.mapper;

import com.decade.practice.engagement.api.ChatPolicyInfo;
import com.decade.practice.engagement.domain.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatPolicyMapper {

      @Mapping(source = "policy.chatId", target = "identifier")
      @Mapping(target = "creators", expression = "java(policy.getCreators().members().toList())")
      ChatPolicyInfo map(Chat policy);

}
