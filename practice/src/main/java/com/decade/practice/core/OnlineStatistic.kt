package com.decade.practice.core

import com.decade.practice.model.domain.entity.User
import com.decade.practice.online.model.OnlineStatus
import java.time.Instant

interface OnlineStatistic {
      fun set(user: User, at: Long = Instant.now().epochSecond): OnlineStatus
      fun get(username: String): OnlineStatus
      fun getOnlineList(username: String): List<OnlineStatus>
}