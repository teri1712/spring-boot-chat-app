package com.decade.practice.utils

import org.springframework.http.CacheControl
import java.util.concurrent.TimeUnit

object CacheUtils {
      val defaultCacheControl = CacheControl
            .maxAge(30, TimeUnit.DAYS)
}