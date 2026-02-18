package com.decade.practice.engagement.api.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconEventPlaced extends EventPlaced {


    private final Integer iconId;

}
