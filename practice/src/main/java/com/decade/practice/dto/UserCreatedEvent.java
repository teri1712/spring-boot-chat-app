package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private UUID userId;
    private String username;
    private String name;
    private Float gender;
    private Date dob;
    private ImageSpec avatar;
}
