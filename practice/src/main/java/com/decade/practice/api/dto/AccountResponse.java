package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.entities.SyncContext;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountResponse {

    private UserResponse user;
    private SyncContext syncContext;

}