package com.decade.practice.online

import com.decade.practice.online.model.OnlineStatus
import org.springframework.data.repository.CrudRepository

interface OnlineRepository : CrudRepository<OnlineStatus, String>