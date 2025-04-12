package com.decade.practice.database.repository

import org.springframework.data.jpa.repository.JpaRepository

fun <T : Any, I : Any> JpaRepository<T, I>.get(id: I): T {
    return findById(id).orElseThrow()
}

