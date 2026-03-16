package com.decade.practice.inbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record HashValue(

          @Column(name = "hash_value")
          Long value) {

      public HashValue {
            if (value >= mod) {
                  value %= mod;
            }
      }

      private static final Long p = 31L, mod = 998_244_353L;
      public static final HashValue ONE = new HashValue(1L);

      HashValue shift() {
            return new HashValue((value * p) % mod);
      }

      HashValue plus(HashValue other) {
            long newValue = value + other.value();
            if (newValue >= mod) newValue -= mod;
            return new HashValue(newValue);
      }

      HashValue minus(HashValue other) {
            long newValue = value - other.value();
            if (newValue < 0) newValue += mod;
            return new HashValue(newValue);
      }

      HashValue times(HashValue other) {
            long newValue = (value * other.value()) % mod;
            return new HashValue(newValue);
      }

      public HashValue shift(long exp) {
            long pow = 1;
            long base = p;
            base %= mod;

            while (exp > 0) {
                  if ((exp & 1) == 1) {
                        pow = (pow * base) % mod;
                  }
                  base = (base * base) % mod;
                  exp >>= 1;
            }

            return new HashValue((value * pow) % mod);
      }
}
