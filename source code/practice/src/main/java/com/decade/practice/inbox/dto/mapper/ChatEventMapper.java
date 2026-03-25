package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.domain.RoomEvent;
import com.decade.practice.inbox.dto.PostingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatEventMapper {

      PostingResponse toResponse(RoomEvent roomEvent);

}
