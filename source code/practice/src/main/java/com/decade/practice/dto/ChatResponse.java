package com.decade.practice.dto;

import java.io.Serializable;
import java.util.UUID;

// TODO: Adjust client
public record ChatResponse(String identifier, UUID owner, UUID partner) implements Serializable {

}
