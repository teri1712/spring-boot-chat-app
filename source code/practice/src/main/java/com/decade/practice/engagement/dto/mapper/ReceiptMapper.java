package com.decade.practice.engagement.dto.mapper;

import com.decade.practice.engagement.application.ports.in.*;
import com.decade.practice.engagement.domain.*;
import com.decade.practice.engagement.dto.ReceiptResponse;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReceiptMapper {

    ReceiptResponse toResponse(Receipt receipt);

    @SubclassMapping(source = TextCommand.class, target = TextReceipt.class)
    @SubclassMapping(source = IconCommand.class, target = IconReceipt.class)
    @SubclassMapping(source = FileCommand.class, target = FileReceipt.class)
    @SubclassMapping(source = ImageCommand.class, target = ImageReceipt.class)
    @SubclassMapping(source = SeenCommand.class, target = SeenReceipt.class)
    @SubclassMapping(source = PreferenceCommand.class, target = PreferenceReceipt.class)
    Receipt toEntity(EventCommand command);


}
