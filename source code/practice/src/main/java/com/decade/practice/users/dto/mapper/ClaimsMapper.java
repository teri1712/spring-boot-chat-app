package com.decade.practice.users.dto.mapper;

import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.web.security.UserClaims;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClaimsMapper {
    UserClaims toClaims(ProfileResponse profileResponse);
}
