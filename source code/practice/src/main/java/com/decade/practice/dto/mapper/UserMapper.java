package com.decade.practice.dto.mapper;

import com.decade.practice.dto.UserResponse;
import com.decade.practice.persistence.jpa.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ImageMapper.class)
public interface UserMapper {
    
    UserResponse toResponse(User user);
}
