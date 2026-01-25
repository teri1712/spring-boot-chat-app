package com.decade.practice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
//TODO: Adjust client
public class AccountEntryResponse {
    private AccountResponse account;
    private TokenCredential tokenCredential;
}