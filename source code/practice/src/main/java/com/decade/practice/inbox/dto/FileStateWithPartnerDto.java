package com.decade.practice.inbox.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
public class FileStateWithPartnerDto extends MessageStateWithPartnerDto {
    private final String filename;
    private final Integer size;
    private final String uri;
}
