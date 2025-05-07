package com.decade.practice.core

import com.decade.practice.model.OnlineStatus
import com.decade.practice.model.domain.entity.User
import java.time.Instant

interface OnlineStatistic {
      fun set(user: User, at: Long = Instant.now().epochSecond): OnlineStatus
      fun get(username: String): OnlineStatus
      fun getOnlineList(username: String): List<OnlineStatus>
}