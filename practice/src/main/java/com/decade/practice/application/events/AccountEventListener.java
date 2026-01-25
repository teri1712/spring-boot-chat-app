package com.decade.practice.application.events;

import com.decade.practice.persistence.jpa.entities.User;

/**
 * Interface for listening to account-related events.
 */
public interface AccountEventListener {

    /**
     * Called after an account is created.
     *
     * @param account the user account
     * @param success whether the operation was successful
     */
    default void afterAccountCreated(User account, boolean success) {
    }

    /**
     * Called after a password is changed.
     *
     * @param account the user account
     * @param success whether the operation was successful
     */
    default void afterPasswordChanged(User account, boolean success) {
    }

    /**
     * Called before an account is created.
     *
     * @param account the user account
     */
    default void beforeAccountCreated(User account) {
    }

    /**
     * Called before a password is changed.
     *
     * @param account the user account
     */
    default void beforePasswordChanged(User account) {
    }
}