package com.decade.practice.database.repository

import com.decade.practice.model.domain.entity.User
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {

      @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
      fun getByUsername(username: String): User

      @Lock(LockModeType.OPTIMISTIC)
      @Query("SELECT u FROM User u WHERE u.id = :id")
      fun getOptimistic(id: UUID): User

      @Lock(LockModeType.PESSIMISTIC_WRITE)
      @Query("SELECT u FROM User u WHERE u.id = :id")
      fun getPessimisticWrite(id: UUID): User

      @Lock(LockModeType.PESSIMISTIC_WRITE)
      @Query("SELECT u FROM User u WHERE u.username = :username")
      fun getPessimisticWrite(username: String): User

      @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
      @Query("SELECT u FROM User u WHERE u.username = :username")
      fun getOptimisticIncrement(username: String): User

      fun findByNameContainingAndRole(name: String, role: String): List<User>

}
