package com.decade.practice.dto.mapper;

import com.decade.practice.dto.ImageRequest;
import com.decade.practice.dto.ImageResponse;
import com.decade.practice.persistence.jpa.embeddables.ImageSpecEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageMapper {

    ImageSpecEmbeddable toEntity(ImageRequest request);

    ImageResponse toResponse(ImageSpecEmbeddable imageSpec);
}
