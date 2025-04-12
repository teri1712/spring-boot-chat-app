package com.decade.practice.stat

import com.decade.practice.stat.model.OnlineStatus

interface OnlineStatistic {
    fun get(
        username: String
    ): OnlineStatus

    fun getOnlineList(
        username: String
    ): List<OnlineStatus>
}