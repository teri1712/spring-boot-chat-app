package com.decade.practice.event

import com.decade.practice.model.domain.entity.User

interface AccountEventListener {

      fun afterAccountCreated(account: User, success: Boolean) {}
      fun afterPasswordChanged(account: User, success: Boolean) {}

      fun beforeAccountCreated(account: User) {}
      fun beforePasswordChanged(account: User) {}
}     