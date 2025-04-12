package com.decade.practice.stat

import com.decade.practice.stat.model.OnlineStatus
import org.springframework.data.repository.CrudRepository

interface OnlineRepository : CrudRepository<OnlineStatus, String>