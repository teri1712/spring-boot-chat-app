package com.decade.practice.database.repository

import com.decade.practice.model.entity.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AdminRepository : JpaRepository<Admin, UUID>


fun AdminRepository.getOrNull(): Admin? {
    return findAll().getOrNull(0)
}

fun AdminRepository.get(): Admin {
    return findAll().getOrNull(0)!!
}