package com.decade.practice.dto.mapper;

import com.decade.practice.dto.UserResponse;
import com.decade.practice.infra.security.models.UserClaims;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClaimsMapper {
    UserClaims toClaims(UserResponse userResponse);
}
