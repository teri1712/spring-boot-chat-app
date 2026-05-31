package com.decade.practice.users.dto.mapper;

import com.decade.practice.users.domain.User;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.utils.GenderUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

      ProfileResponse map(User user);

      default String toGender(Float gender) {
            return GenderUtils.inspect(gender);
      }
}
