package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.entities.SyncContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AccountResponse {

    private UserResponse user;
    private SyncContext syncContext;

}