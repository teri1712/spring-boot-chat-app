package com.decade.practice.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccountResponse {
    private UserResponse account;
    private TokenCredential tokenCredential;
}