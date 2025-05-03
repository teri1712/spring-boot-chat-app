package com.decade.practice.online

import com.decade.practice.online.model.OnlineStatus

interface OnlineStatistic {

      fun get(username: String): OnlineStatus
      fun getOnlineList(username: String): List<OnlineStatus>
}