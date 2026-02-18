package com.decade.practice.engagement.dto.mapper;

import com.decade.practice.engagement.application.ports.in.*;
import com.decade.practice.engagement.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommandMappers {

    TextCommand toText(TextRequest request, UUID idempotentKey, UUID senderId, String chatId);

    ImageCommand toImage(ImageRequest request, UUID idempotentKey, UUID senderId, String chatId);

    IconCommand toIcon(IconRequest request, UUID idempotentKey, UUID senderId, String chatId);

    SeenCommand toSeen(SeenRequest request, UUID idempotentKey, UUID senderId, String chatId);

    FileCommand toFile(FileRequest request, UUID idempotentKey, UUID senderId, String chatId);

    PreferenceCommand toPreference(PreferenceRequest request, UUID idempotentKey, UUID senderId, String chatId);


}
