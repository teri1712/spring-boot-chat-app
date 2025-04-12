package com.decade.practice.event

import com.decade.practice.model.entity.User
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

open class TransactionalCompletionAccountListener : AccountEventListener {
    override fun onAccountCreated(account: User) {
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    handleAccountCreated(account)
                }
            })
    }

    override fun onPasswordChanged(account: User) {
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    handlePasswordChanged(account)
                }
            })
    }

    open fun handleAccountCreated(account: User) {}
    open fun handlePasswordChanged(account: User) {}

}