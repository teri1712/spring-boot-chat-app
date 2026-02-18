package com.decade.practice.engagement.api.events;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@JsonTypeName("TEXT")
@Getter
@SuperBuilder

public class TextEventPlaced extends EventPlaced {

    private final String content;

}
