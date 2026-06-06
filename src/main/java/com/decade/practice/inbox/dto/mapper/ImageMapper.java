package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.domain.ImageSpec;
import com.decade.practice.inbox.dto.ImageSpecResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageMapper {

      ImageSpecResponse toResponse(ImageSpec imageSpec);
}
