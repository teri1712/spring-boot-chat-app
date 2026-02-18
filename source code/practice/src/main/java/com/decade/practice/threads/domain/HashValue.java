package com.decade.practice.threads.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record HashValue(Long value) {

    public HashValue {
        if (value >= mod) {
            value %= mod;
        }
    }

    private static final Long p = 31L, mod = 998_244_353L;

    HashValue plus(HashValue other) {
        return new HashValue((value * p + other.value) % mod);
    }
}
