package com.decade.practice.database.repository;

import com.decade.practice.model.domain.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

      @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
      User getByUsername(String username);

      @Lock(LockModeType.OPTIMISTIC)
      @Query("SELECT u FROM User u WHERE u.id = :id")
      User getOptimistic(@Param("id") UUID id);

      @Lock(LockModeType.PESSIMISTIC_WRITE)
      @Query("SELECT u FROM User u WHERE u.id = :id")
      User getPessimisticWrite(@Param("id") UUID id);

      @Lock(LockModeType.PESSIMISTIC_WRITE)
      @Query("SELECT u FROM User u WHERE u.username = :username")
      User getPessimisticWrite(@Param("username") String username);

      @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
      @Query("SELECT u FROM User u WHERE u.username = :username")
      User getOptimisticIncrement(@Param("username") String username);

      List<User> findByNameContainingAndRole(String name, String role);
}