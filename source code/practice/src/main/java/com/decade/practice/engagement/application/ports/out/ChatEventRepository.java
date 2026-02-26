package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ChatEventRepository extends JpaRepository<ChatEvent, UUID> {
}