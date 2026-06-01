package com.decade.practice.inbox.dto.mapper;

import com.decade.practice.inbox.application.ports.in.*;
import com.decade.practice.inbox.domain.SeenRequest;
import com.decade.practice.inbox.domain.TextRequest;
import com.decade.practice.inbox.dto.FileRequest;
import com.decade.practice.inbox.dto.IconRequest;
import com.decade.practice.inbox.dto.ImageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommandMappers {

      TextCommand toText(TextRequest request, UUID postingId, UUID senderId, String chatId);

      ImageCommand toImage(ImageRequest request, UUID postingId, UUID senderId, String chatId);

      IconCommand toIcon(IconRequest request, UUID postingId, UUID senderId, String chatId);

      SeenCommand toSeen(SeenRequest request, UUID postingId, UUID senderId, String chatId);

      FileCommand toFile(FileRequest request, UUID postingId, UUID senderId, String chatId);


}
