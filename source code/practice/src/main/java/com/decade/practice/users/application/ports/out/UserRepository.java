package com.decade.practice.users.application.ports.out;

import com.decade.practice.users.api.UserInfo;
import com.decade.practice.users.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Stream<UserInfo> findByIdIn(Set<UUID> ids);

}