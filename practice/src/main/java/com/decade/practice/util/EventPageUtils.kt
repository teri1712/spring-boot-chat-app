package com.decade.practice.util

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

object EventPageUtils {
      private const val EVENT_LIMIT: Int = 20
      private val VERSION_SORT_ASC: Sort = Sort.by(Sort.Direction.ASC, "eventVersion")
      private val VERSION_SORT_DESC: Sort = Sort.by(Sort.Direction.DESC, "eventVersion")
      val pageEvent = PageRequest.of(0, EVENT_LIMIT, VERSION_SORT_DESC)
      val headEvent = PageRequest.of(0, 1, VERSION_SORT_DESC)
}

