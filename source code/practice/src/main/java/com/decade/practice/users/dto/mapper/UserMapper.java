package com.decade.practice.users.dto.mapper;

import com.decade.practice.users.domain.User;
import com.decade.practice.users.dto.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    ProfileResponse toResponse(User user);
}
