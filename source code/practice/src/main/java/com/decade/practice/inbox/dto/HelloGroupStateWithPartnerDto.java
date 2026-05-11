package com.decade.practice.inbox.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
public class HelloGroupStateWithPartnerDto extends MessageStateWithPartnerDto {
    private final PartnerResponse creator;
}
