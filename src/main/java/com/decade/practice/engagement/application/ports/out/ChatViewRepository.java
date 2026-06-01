package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ChatViewRepository extends JpaRepository<Chat, String> {

    @Override
    Optional<Chat> findById(String s);
}