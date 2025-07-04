package com.decade.practice.utils;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

public class CacheUtils {

      public static final CacheControl DEFAULT_CACHE_CONTROL = CacheControl
            .maxAge(30, TimeUnit.DAYS);

}