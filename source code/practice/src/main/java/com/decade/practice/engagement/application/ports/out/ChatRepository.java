package com.decade.practice.engagement.application.ports.out;

import com.decade.practice.engagement.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {


}