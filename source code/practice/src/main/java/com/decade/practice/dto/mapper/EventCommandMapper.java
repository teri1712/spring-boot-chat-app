package com.decade.practice.dto.mapper;

import com.decade.practice.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventCommandMapper {

    TextEventCreateCommand toText(TextEventRequest eventRequest, String chatId, UUID senderId);

    ImageEventCreateCommand toImage(ImageEventCreateRequest eventRequest, String chatId, UUID senderId);

    IconEventCreateCommand toIcon(IconEventCreateRequest eventRequest, String chatId, UUID senderId);

    SeenEventCreateCommand toSeen(SeenEventCreateRequest eventRequest, String chatId, UUID senderId);

    PreferenceCreateCommand toPreference(PreferenceCreateRequest eventRequest, String chatId, UUID senderId);

    FileEventCreateCommand toFile(FileEventCreateRequest eventRequest, String chatId, UUID senderId);
}
