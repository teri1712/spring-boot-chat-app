package com.decade.practice.utils;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

public class WebCacheUtils {

        public static final CacheControl ONE_MONTHS = CacheControl
                .maxAge(30, TimeUnit.DAYS);

}