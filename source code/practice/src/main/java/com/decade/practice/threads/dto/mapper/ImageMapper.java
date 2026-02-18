package com.decade.practice.threads.dto.mapper;

import com.decade.practice.threads.domain.ImageSpec;
import com.decade.practice.threads.dto.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageMapper {

    ImageResponse toResponse(ImageSpec imageSpec);
}
