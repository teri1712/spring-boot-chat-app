package com.decade.practice.domain.repositories;

import com.decade.practice.domain.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface UserRepository extends CrudRepository<User, UUID> {

        User findByUsername(String username);

        User findByIdWithPessimisticWrite(UUID id);

        User findByUsernameWithPessimisticWrite(String username);

        List<User> findByNameContainingAndRole(String name, String role);
}