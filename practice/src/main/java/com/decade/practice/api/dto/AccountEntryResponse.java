package com.decade.practice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccountEntryResponse {
    private AccountResponse account;
    private TokenCredential tokenCredential;
}