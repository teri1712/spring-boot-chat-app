package com.decade.practice.adapter.repositories;

import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.UserRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, UUID> {

        @Override
        @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
        User findByUsername(String username);

        @Override
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT u FROM User u WHERE u.id = :id")
        User findByIdWithPessimisticWrite(@Param("id") UUID id);

        @Override
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT u FROM User u WHERE u.username = :username")
        User findByUsernameWithPessimisticWrite(@Param("username") String username);

        @Override
        List<User> findByNameContainingAndRole(String name, String role);
}
