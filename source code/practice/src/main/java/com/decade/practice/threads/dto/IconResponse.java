package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconResponse extends EventResponse {

    private final Integer iconId;

}
