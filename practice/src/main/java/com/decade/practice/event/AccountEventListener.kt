package com.decade.practice.event

import com.decade.practice.model.entity.User

interface AccountEventListener {
    fun onAccountCreated(account: User)
    fun onPasswordChanged(account: User)
}     