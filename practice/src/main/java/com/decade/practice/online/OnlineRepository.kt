package com.decade.practice.online

import com.decade.practice.model.OnlineStatus
import org.springframework.data.repository.CrudRepository

interface OnlineRepository : CrudRepository<OnlineStatus, String>